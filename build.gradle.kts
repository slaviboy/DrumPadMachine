import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import entities.Dependency.App
import entities.Dependency.Audio
import entities.Dependency.IOLib
import entities.Dependency.Oboe
import entities.Dependency.ParseLib
import entities.DependencyType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.1.0-rc01" apply false
    id("com.android.library") version "8.1.0-rc01" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    //id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}

val subProjects = listOf(App, Audio, IOLib, Oboe, ParseLib)

fun getNamespace(name: String): String {
    return "com.slaviboy.$name"
}

fun BaseExtension.setCompileOptions() {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

@Suppress("UnstableApiUsage")
fun BaseExtension.general() {

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
        /*https://stackoverflow.com/a/75739046/3753104
        compilerOptions {
            freeCompilerArgs.add("-Xdebug")
        }*/
    }

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = ApplicationDependencyVersions.kotlinCompiler
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun BaseExtension.setDefaultConfig(name: String) {
    apply {
        namespace = getNamespace(name)
        compileSdkVersion(ApplicationConfiguration.compileSdk)

        defaultConfig {
            minSdk = ApplicationConfiguration.minSdk
            targetSdk = ApplicationConfiguration.targetSdk
            testInstrumentationRunner = ApplicationConfiguration.androidTestInstrumentation
            vectorDrawables {
                useSupportLibrary = true
            }
        }
    }
}

fun BaseExtension.setDefaultConfigJNILibraries() {
    apply {
        defaultConfig {
            externalNativeBuild {
                cmake {
                    cppFlags("")
                    arguments("-DANDROID_STL=c++_shared")
                }
            }
        }
        externalNativeBuild {
            cmake {
                path("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
    }
}

fun BaseExtension.setBuildTypes() {
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }
}

fun BaseExtension.setup(name: String) {
    setDefaultConfig(name)
    // setBuildTypes()
    setCompileOptions()
    general()
}

fun PluginAware.setPlugins() {
    apply {
        plugin("kotlin-kapt")
        plugin("org.jetbrains.kotlin.android")
        plugin("com.google.dagger.hilt.android")
    }
}

fun PluginAware.setApplicationPlugins() {
    setPlugins()
    apply {
        plugin("com.android.application")
        // plugin("com.google.gms.google-services")
        plugin("com.google.firebase.crashlytics")
    }
}

fun PluginAware.setLibraryPlugins() {
    setPlugins()
    apply {
        plugin("com.android.library")
    }
}

fun PluginAware.setLibraryJNIPlugins() {
    apply {
        plugin("com.android.library")
    }
}

fun Project.setDependencies() {
    dependencies {
        kapt(*ApplicationDependencies.kapt)
        //ksp(*ApplicationDependencies.ksp)
    }
}

fun Project.setResDirs() {
    android.sourceSets.getByName("main") {
        res.srcDirs(
            "/src/main/res",
            "/src/main/res/fonts/ano",
            "/src/main/res/fonts/roboto",
            "/src/main/res/fonts/cards"
        )
    }
}

fun Project.setVariants() {
    android.buildFeatures.buildConfig = true
    android.apply {
        flavorDimensions("environment")
        productFlavors {
            create("dev") {
                applicationIdSuffix = ".dev"
                versionNameSuffix = "-dev"
                dimension = "environment"
                buildConfigField("String", "BUILD_VARIANT", "\"dev\"")
                resValue("string", "app_name", "Postbank Dev")
                manifestPlaceholders(
                    mapOf(
                        "icon" to "@mipmap/ic_launcher_dev",
                        "roundIcon" to "@mipmap/ic_launcher_dev_round"
                    )
                )
            }

            create("beta") {
                applicationIdSuffix = ".beta"
                versionNameSuffix = "-beta"
                dimension = "environment"
                buildConfigField("String", "BUILD_VARIANT", "\"beta\"")
                resValue("string", "app_name", "Postbank Beta")
                manifestPlaceholders(
                    mapOf(
                        "icon" to "@mipmap/ic_launcher_dev",
                        "roundIcon" to "@mipmap/ic_launcher_dev_round"
                    )
                )
            }

            create("prod") {
                applicationIdSuffix = ""
                versionNameSuffix = ""
                dimension = "environment"
                buildConfigField("String", "BUILD_VARIANT", "\"prod\"")
                resValue("string", "app_name", "Postbank")
                manifestPlaceholders(
                    mapOf(
                        "icon" to "@mipmap/ic_launcher",
                        "roundIcon" to "@mipmap/ic_launcher_round"
                    )
                )
            }
        }
    }
}

subprojects {
    subProjects.find {
        println("subproject- $path - $name")
        it.matches(this)
    }?.let {
        when (it.type) {
            is DependencyType.Application -> {
                setApplicationPlugins()
                configure<BaseExtension> {
                    apply {
                        defaultConfig {
                            applicationId = getNamespace(ApplicationConfiguration.appName)
                            versionCode = ApplicationConfiguration.versionCode
                            versionName = ApplicationConfiguration.versionName
                        }
                    }
                    setup(ApplicationConfiguration.appName)
                }
                setDependencies()
                setResDirs()
                // setVariants()
            }

            is DependencyType.Library -> {
                setLibraryPlugins()
                configure<LibraryExtension> {
                    setup(name)
                    setDefaultConfigJNILibraries()
                }
                setDependencies()
                android.buildFeatures.buildConfig = true
                android.buildFeatures.prefab = true
            }

            is DependencyType.LibraryJNI -> {
                setLibraryJNIPlugins()
                configure<LibraryExtension> {
                    setup(name)
                    setDefaultConfigJNILibraries()
                }
                android.buildFeatures.buildConfig = true
                android.buildFeatures.prefab = true
            }

            is DependencyType.Kotlin -> {

            }

            else -> {

            }
        }
    }
}

val Project.android: BaseExtension
    get() = (this as? ExtensionAware)?.extensions?.getByName("android") as BaseExtension