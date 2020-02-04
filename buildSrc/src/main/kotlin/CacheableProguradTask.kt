/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*


@CacheableTask
open class CacheableProguardTask : proguard.gradle.ProGuardTask() {

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getLibraryJarFileCollection(): FileCollection = super.getLibraryJarFileCollection()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getConfigurationFileCollection(): FileCollection = super.getConfigurationFileCollection()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getInJarFileCollection(): FileCollection = super.getInJarFileCollection()

    
    @Internal
    override fun getOutJarFilters(): MutableList<Any?> = super.getOutJarFilters()

    @Internal
    override fun getrenamesourcefileattribute(): Any? = super.getrenamesourcefileattribute()

    @Internal
    override fun getprintusage(): Any? = super.getprintusage()

    @Internal
    override fun getforceprocessing(): Any? = super.getforceprocessing()

    @Internal
    override fun getInJarFilters(): MutableList<Any?> = super.getInJarFilters()

    @Internal
    override fun getdontwarn(): Any? = super.getdontwarn()

    @Internal
    override fun getaddconfigurationdebugging(): Any? = super.getaddconfigurationdebugging()

    @Internal
    override fun getallowaccessmodification(): Any? = super.getallowaccessmodification()

    @Internal
    override fun getignorewarnings(): Any? = super.getignorewarnings()

    @Internal
    override fun getkeepdirectories(): Any? = super.getkeepdirectories()

    @Internal
    override fun getuseuniqueclassmembernames(): Any? = super.getuseuniqueclassmembernames()

    @Internal
    override fun getmicroedition(): Any? = super.getmicroedition()

    @Internal
    override fun getandroid(): Any? = super.getandroid()

    @Internal
    override fun getoverloadaggressively(): Any? = super.getoverloadaggressively()

    @Internal
    override fun getdontusemixedcaseclassnames(): Any? = super.getdontusemixedcaseclassnames()

    @Internal
    override fun getdontnote(): Any? = super.getdontnote()

    @Internal
    override fun getInJarFiles(): MutableList<Any?> = super.getInJarFiles()

    @Internal
    override fun getInJarCounts(): MutableList<Any?> = super.getInJarCounts()

    @Internal
    override fun getdontpreverify(): Any? = super.getdontpreverify()

    @Internal
    override fun getverbose(): Any? = super.getverbose()

    @Internal
    override fun getskipnonpubliclibraryclasses(): Any? = super.getskipnonpubliclibraryclasses()

    @Internal
    override fun getdontoptimize(): Any? = super.getdontoptimize()

    @Internal
    override fun getdump(): Any? = super.getdump()

    @Internal
    override fun getdontobfuscate(): Any? = super.getdontobfuscate()

    @Internal
    override fun getLibraryJarFilters(): MutableList<Any?> = super.getLibraryJarFilters()

    @Internal
    override fun getprintmapping(): Any? = super.getprintmapping()

    @Internal
    override fun getdontshrink(): Any? = super.getdontshrink()

    @Internal
    override fun getkeepattributes(): Any? = super.getkeepattributes()

    @Internal
    override fun getOutJarFileCollection(): FileCollection = super.getOutJarFileCollection()

    @Internal
    override fun getdontskipnonpubliclibraryclassmembers(): Any? = super.getdontskipnonpubliclibraryclassmembers()

    @Internal
    override fun getprintconfiguration(): Any? = super.getprintconfiguration()

    @Internal
    override fun getmergeinterfacesaggressively(): Any? = super.getmergeinterfacesaggressively()

    @Internal
    override fun getConfigurationFiles(): MutableList<Any?> = super.getConfigurationFiles()

    @Internal
    override fun getkeeppackagenames(): Any? = super.getkeeppackagenames()

    @Internal
    override fun getprintseeds(): Any? = super.getprintseeds()

    @Internal
    override fun getadaptresourcefilenames(): Any? = super.getadaptresourcefilenames()

    @Internal
    override fun getrepackageclasses(): Any? = super.getrepackageclasses()

    @Internal
    override fun getadaptresourcefilecontents(): Any? = super.getadaptresourcefilecontents()

    @Internal
    override fun getflattenpackagehierarchy(): Any? = super.getflattenpackagehierarchy()

    @Internal
    override fun getadaptclassstrings(): Any? = super.getadaptclassstrings()

    @Internal
    override fun getLibraryJarFiles(): MutableList<Any?> = super.getLibraryJarFiles()

    @Internal
    override fun getkeepparameternames(): Any? = super.getkeepparameternames()

    @Internal
    override fun getOutJarFiles(): MutableList<Any?> = super.getOutJarFiles()
}
