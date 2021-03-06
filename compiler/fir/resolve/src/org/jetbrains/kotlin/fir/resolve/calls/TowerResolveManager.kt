/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls

import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.impl.FirQualifiedAccessExpressionImpl
import org.jetbrains.kotlin.fir.resolve.constructClassType
import org.jetbrains.kotlin.fir.resolve.transformQualifiedAccessUsingSmartcastInfo
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.firUnsafe
import org.jetbrains.kotlin.fir.scopes.ProcessorAction
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.impl.FirImplicitBuiltinTypeRef
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind
import org.jetbrains.kotlin.types.AbstractTypeChecker
import org.jetbrains.kotlin.util.OperatorNameConventions
import java.util.*

class TowerResolveManager internal constructor(private val towerResolver: FirTowerResolver) {
    private val queue = PriorityQueue<TowerInvokeResolveQuery>()

    private var group = TowerGroup.Start

    lateinit var candidateFactory: CandidateFactory

    lateinit var invokeOnGivenReceiverCandidateFactory: CandidateFactory

    lateinit var invokeReceiverCandidateFactory: CandidateFactory

    lateinit var invokeBuiltinExtensionReceiverCandidateFactory: CandidateFactory

    lateinit var stubReceiverCandidateFactory: CandidateFactory

    lateinit var resultCollector: CandidateCollector

    lateinit var invokeReceiverCollector: CandidateCollector

    private class TowerScopeLevelProcessor(
        val explicitReceiver: FirExpression?,
        val explicitReceiverKind: ExplicitReceiverKind,
        val resultCollector: CandidateCollector,
        val candidateFactory: CandidateFactory,
        val group: TowerGroup
    ) : TowerScopeLevel.TowerScopeLevelProcessor<AbstractFirBasedSymbol<*>> {
        override fun consumeCandidate(
            symbol: AbstractFirBasedSymbol<*>,
            dispatchReceiverValue: ReceiverValue?,
            implicitExtensionReceiverValue: ImplicitReceiverValue<*>?,
            builtInExtensionFunctionReceiverValue: ReceiverValue?
        ) {
            // Check explicit extension receiver for default package members
            if (symbol is FirNamedFunctionSymbol && dispatchReceiverValue == null &&
                (implicitExtensionReceiverValue == null) != (explicitReceiver == null) &&
                explicitReceiver !is FirResolvedQualifier &&
                symbol.callableId.packageName.startsWith(defaultPackage)
            ) {
                val extensionReceiverType = explicitReceiver?.typeRef?.coneTypeSafe()
                    ?: implicitExtensionReceiverValue?.type as? ConeClassLikeType
                if (extensionReceiverType != null) {
                    val declarationReceiverTypeRef =
                        (symbol as? FirCallableSymbol<*>)?.fir?.receiverTypeRef as? FirResolvedTypeRef
                    val declarationReceiverType = declarationReceiverTypeRef?.type
                    if (declarationReceiverType is ConeClassLikeType) {
                        if (!AbstractTypeChecker.isSubtypeOf(
                                candidateFactory.bodyResolveComponents.inferenceComponents.ctx,
                                extensionReceiverType,
                                declarationReceiverType.lookupTag.constructClassType(
                                    declarationReceiverType.typeArguments.map { ConeStarProjection }.toTypedArray(),
                                    isNullable = true
                                )
                            )
                        ) {
                            return
                        }
                    }
                }
            }
            // ---
            resultCollector.consumeCandidate(
                group, candidateFactory.createCandidate(
                    symbol,
                    explicitReceiverKind,
                    dispatchReceiverValue,
                    implicitExtensionReceiverValue,
                    builtInExtensionFunctionReceiverValue
                )
            )
        }

        companion object {
            val defaultPackage = Name.identifier("kotlin")
        }
    }


    private inner class LevelHandler(
        val info: CallInfo,
        val explicitReceiverKind: ExplicitReceiverKind,
        val group: TowerGroup
    ) {
        private fun createExplicitReceiverForInvoke(candidate: Candidate): FirQualifiedAccessExpressionImpl {
            val symbol = candidate.symbol as FirCallableSymbol<*>
            return FirQualifiedAccessExpressionImpl(null).apply {
                calleeReference = FirNamedReferenceWithCandidate(
                    null,
                    symbol.callableId.callableName,
                    candidate
                )
                dispatchReceiver = candidate.dispatchReceiverExpression()
                typeRef = towerResolver.typeCalculator.tryCalculateReturnType(symbol.firUnsafe())
            }
        }

        fun SessionBasedTowerLevel.handleLevel(invokeResolveMode: InvokeResolveMode? = null): ProcessorAction {
            var result = ProcessorAction.NONE
            val processor =
                TowerScopeLevelProcessor(
                    info.explicitReceiver,
                    explicitReceiverKind,
                    resultCollector,
                    // TODO: performance?
                    if (invokeResolveMode == InvokeResolveMode.IMPLICIT_CALL_ON_GIVEN_RECEIVER) {
                        invokeOnGivenReceiverCandidateFactory
                    } else candidateFactory,
                    group
                )
            when (info.callKind) {
                CallKind.VariableAccess -> {
                    result += processElementsByName(TowerScopeLevel.Token.Properties, info.name, processor)
                    // TODO: more accurate condition, or process properties/object in some other way
                    if (!resultCollector.isSuccess() &&
                        (this !is ScopeTowerLevel || this.extensionReceiver == null)
                    ) {
                        result += processElementsByName(TowerScopeLevel.Token.Objects, info.name, processor)
                    }
                }
                CallKind.Function -> {
                    val invokeBuiltinExtensionMode =
                        invokeResolveMode == InvokeResolveMode.RECEIVER_FOR_INVOKE_BUILTIN_EXTENSION
                    if (!invokeBuiltinExtensionMode) {
                        result += processElementsByName(TowerScopeLevel.Token.Functions, info.name, processor)
                    }
                    if (invokeResolveMode == InvokeResolveMode.IMPLICIT_CALL_ON_GIVEN_RECEIVER ||
                        resultCollector.isSuccess()
                    ) {
                        return result
                    }

                    val invokeReceiverProcessor = TowerScopeLevelProcessor(
                        info.explicitReceiver,
                        explicitReceiverKind,
                        invokeReceiverCollector,
                        if (invokeBuiltinExtensionMode) invokeBuiltinExtensionReceiverCandidateFactory
                        else invokeReceiverCandidateFactory,
                        group
                    )
                    invokeReceiverCollector.newDataSet()
                    result += processElementsByName(TowerScopeLevel.Token.Properties, info.name, invokeReceiverProcessor)

                    if (invokeReceiverCollector.isSuccess()) {
                        for (invokeReceiverCandidate in invokeReceiverCollector.bestCandidates()) {

                            val symbol = invokeReceiverCandidate.symbol as FirCallableSymbol<*>
                            val isExtensionFunctionType = symbol.fir.returnTypeRef.isExtensionFunctionType()
                            if (invokeBuiltinExtensionMode && !isExtensionFunctionType) {
                                continue
                            }
                            val extensionReceiverExpression = invokeReceiverCandidate.extensionReceiverExpression()
                            val useImplicitReceiverAsBuiltinInvokeArgument =
                                !invokeBuiltinExtensionMode && isExtensionFunctionType &&
                                        invokeReceiverCandidate.explicitReceiverKind == ExplicitReceiverKind.NO_EXPLICIT_RECEIVER

                            val invokeReceiverExpression = createExplicitReceiverForInvoke(invokeReceiverCandidate).let {
                                if (!invokeBuiltinExtensionMode) {
                                    it.extensionReceiver = extensionReceiverExpression
                                    // NB: this should fix problem in DFA (KT-36014)
                                    it.explicitReceiver = info.explicitReceiver
                                    it.safe = info.isSafeCall
                                }
                                towerResolver.components.transformQualifiedAccessUsingSmartcastInfo(it)
                            }

                            val invokeFunctionInfo =
                                info.copy(explicitReceiver = invokeReceiverExpression, name = OperatorNameConventions.INVOKE).let {
                                    when {
                                        invokeBuiltinExtensionMode -> it.withReceiverAsArgument(info.explicitReceiver!!)
                                        else -> it
                                    }
                                }

                            val explicitReceiver = ExpressionReceiverValue(invokeReceiverExpression)
                            invokeOnGivenReceiverCandidateFactory = CandidateFactory(towerResolver.components, invokeFunctionInfo)
                            when {
                                invokeBuiltinExtensionMode -> {
                                    towerResolver.enqueueResolverForBuiltinInvokeExtensionWithExplicitArgument(
                                        invokeFunctionInfo, explicitReceiver, this@TowerResolveManager
                                    )
                                }
                                useImplicitReceiverAsBuiltinInvokeArgument -> {
                                    towerResolver.enqueueResolverForBuiltinInvokeExtensionWithImplicitArgument(
                                        invokeFunctionInfo, explicitReceiver, this@TowerResolveManager
                                    )
                                    towerResolver.enqueueResolverForInvoke(
                                        invokeFunctionInfo, explicitReceiver, this@TowerResolveManager
                                    )
                                }
                                else -> {
                                    towerResolver.enqueueResolverForInvoke(
                                        invokeFunctionInfo, explicitReceiver, this@TowerResolveManager
                                    )
                                }
                            }
                        }
                        processQueuedLevelsForInvoke()
                    }
                }
                CallKind.CallableReference -> {
                    val stubReceiver = info.stubReceiver
                    if (stubReceiver != null) {
                        val stubReceiverValue = ExpressionReceiverValue(stubReceiver)
                        val stubProcessor = TowerScopeLevelProcessor(
                            info.explicitReceiver,
                            if (this is MemberScopeTowerLevel && dispatchReceiver is AbstractExplicitReceiver<*>) {
                                ExplicitReceiverKind.DISPATCH_RECEIVER
                            } else {
                                ExplicitReceiverKind.EXTENSION_RECEIVER
                            },
                            resultCollector,
                            stubReceiverCandidateFactory, group
                        )
                        val towerLevelWithStubReceiver = replaceReceiverValue(stubReceiverValue)
                        with(towerLevelWithStubReceiver) {
                            result += processElementsByName(TowerScopeLevel.Token.Functions, info.name, stubProcessor)
                            result += processElementsByName(TowerScopeLevel.Token.Properties, info.name, stubProcessor)
                        }
                        // NB: we don't perform this for implicit Unit
                        if (!resultCollector.isSuccess() && info.explicitReceiver?.typeRef !is FirImplicitBuiltinTypeRef) {
                            result += processElementsByName(TowerScopeLevel.Token.Functions, info.name, processor)
                            result += processElementsByName(TowerScopeLevel.Token.Properties, info.name, processor)
                        }
                    } else {
                        result += processElementsByName(TowerScopeLevel.Token.Functions, info.name, processor)
                        result += processElementsByName(TowerScopeLevel.Token.Properties, info.name, processor)
                    }
                }
                else -> {
                    throw AssertionError("Unsupported call kind in tower resolver: ${info.callKind}")
                }
            }
            return result
        }
    }

    fun reset() {
        queue.clear()
        group = TowerGroup.Start
    }

    fun processLevel(
        towerLevel: SessionBasedTowerLevel,
        callInfo: CallInfo,
        group: TowerGroup,
        explicitReceiverKind: ExplicitReceiverKind = ExplicitReceiverKind.NO_EXPLICIT_RECEIVER
    ): ProcessorAction {
        assert(group > this.group) {
            "Incorrect TowerGroup processing order (c) Mikhail Glukhikh"
        }
        this.group = group
        val result = with(LevelHandler(callInfo, explicitReceiverKind, group)) {
            towerLevel.handleLevel()
        }
        processQueuedLevelsForInvoke()
        return result
    }

    fun enqueueLevelForInvoke(
        towerLevel: SessionBasedTowerLevel,
        callInfo: CallInfo,
        group: TowerGroup,
        mode: InvokeResolveMode,
        explicitReceiverKind: ExplicitReceiverKind = ExplicitReceiverKind.NO_EXPLICIT_RECEIVER
    ) {
        if (callInfo.callKind == CallKind.Function) {
            queue.add(TowerInvokeResolveQuery(towerLevel, callInfo, explicitReceiverKind, group, mode))
        }
    }

    fun processQueuedLevelsForInvoke(groupLimit: TowerGroup = this.group) {
        while (queue.isNotEmpty()) {
            if (queue.peek().group > groupLimit) {
                break
            }
            val query = queue.poll()
            with(LevelHandler(query.callInfo, query.explicitReceiverKind, query.group)) {
                query.towerLevel.handleLevel(invokeResolveMode = query.mode)
            }
        }
    }
}