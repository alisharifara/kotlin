/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*


@CacheableTask
open class CacheableProguardTask : proguard.gradle.ProGuardTask() {

    @Input
    override fun getOutJarFilters(): MutableList<Any?> = super.getOutJarFilters()

    @Optional
    @Input
    override fun getrenamesourcefileattribute(): Any? = super.getrenamesourcefileattribute()

    @Optional
    @Input
    override fun getprintusage(): Any? = super.getprintusage()

    @Optional
    @Input
    override fun getforceprocessing(): Any? = super.getforceprocessing()

    @Input
    override fun getInJarFilters(): MutableList<Any?> = super.getInJarFilters()

    @Optional
    @Input
    override fun getdontwarn(): Any? = super.getdontwarn()

    @Optional
    @Input
    override fun getaddconfigurationdebugging(): Any? = super.getaddconfigurationdebugging()

    @Optional
    @Input
    override fun getallowaccessmodification(): Any? = super.getallowaccessmodification()

    @Optional
    @Input
    override fun getignorewarnings(): Any? = super.getignorewarnings()

    @Optional
    @Input
    override fun getkeepdirectories(): Any? = super.getkeepdirectories()

    @Optional
    @Input
    override fun getuseuniqueclassmembernames(): Any? = super.getuseuniqueclassmembernames()

    @Optional
    @Input
    override fun getmicroedition(): Any? = super.getmicroedition()

    @Optional
    @Input
    override fun getandroid(): Any? = super.getandroid()

    @Optional
    @Input
    override fun getoverloadaggressively(): Any? = super.getoverloadaggressively()

    @Optional
    @Input
    override fun getdontusemixedcaseclassnames(): Any? = super.getdontusemixedcaseclassnames()

    @Optional
    @Input
    override fun getdontnote(): Any? = super.getdontnote()

    @Internal
    override fun getInJarFiles(): MutableList<Any?> = super.getInJarFiles()

    @Input
    override fun getInJarCounts(): MutableList<Any?> = super.getInJarCounts()

    @Optional
    @Input
    override fun getdontpreverify(): Any? = super.getdontpreverify()

    @Optional
    @Input
    override fun getverbose(): Any? = super.getverbose()

    @Optional
    @Input
    override fun getskipnonpubliclibraryclasses(): Any? = super.getskipnonpubliclibraryclasses()

    @Optional
    @Input
    override fun getdontoptimize(): Any? = super.getdontoptimize()

    @Optional
    @Input
    override fun getdump(): Any? = super.getdump()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getInJarFileCollection(): FileCollection = super.getInJarFileCollection()

    @Optional
    @Input
    override fun getdontobfuscate(): Any? = super.getdontobfuscate()

    @Input
    override fun getLibraryJarFilters(): MutableList<Any?> = super.getLibraryJarFilters()

    @Optional
    @Input
    override fun getprintmapping(): Any? = super.getprintmapping()

    @Optional
    @Input
    override fun getdontshrink(): Any? = super.getdontshrink()

    @Optional
    @Input
    override fun getkeepattributes(): Any? = super.getkeepattributes()

    @Internal
    override fun getOutJarFileCollection(): FileCollection = super.getOutJarFileCollection()

    @Optional
    @Input
    override fun getdontskipnonpubliclibraryclassmembers(): Any? = super.getdontskipnonpubliclibraryclassmembers()

    @Optional
    @Input
    override fun getprintconfiguration(): Any? = super.getprintconfiguration()

    @Optional
    @Input
    override fun getmergeinterfacesaggressively(): Any? = super.getmergeinterfacesaggressively()

    @Input
    override fun getConfigurationFiles(): MutableList<Any?> = super.getConfigurationFiles()

    @Optional
    @Input
    override fun getkeeppackagenames(): Any? = super.getkeeppackagenames()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getLibraryJarFileCollection(): FileCollection = super.getLibraryJarFileCollection()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getConfigurationFileCollection(): FileCollection = super.getConfigurationFileCollection()

    @Optional
    @Input
    override fun getprintseeds(): Any? = super.getprintseeds()

    @Optional
    @Input
    override fun getadaptresourcefilenames(): Any? = super.getadaptresourcefilenames()

    @Optional
    @Input
    override fun getrepackageclasses(): Any? = super.getrepackageclasses()

    @Optional
    @Input
    override fun getadaptresourcefilecontents(): Any? = super.getadaptresourcefilecontents()

    @Optional
    @Input
    override fun getflattenpackagehierarchy(): Any? = super.getflattenpackagehierarchy()

    @Optional
    @Input
    override fun getadaptclassstrings(): Any? = super.getadaptclassstrings()

    @Internal
    override fun getLibraryJarFiles(): MutableList<Any?> = super.getLibraryJarFiles()

    @Optional
    @Input
    override fun getkeepparameternames(): Any? = super.getkeepparameternames()

    @Internal
    override fun getOutJarFiles(): MutableList<Any?> = super.getOutJarFiles()
}
