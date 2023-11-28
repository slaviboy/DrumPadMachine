plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

dependencies {
    implementationPlatform(*ApplicationDependencies.dependenciesPlatformApp)
    implementation(*ApplicationDependencies.dependenciesLibrary)
    ksp(*ApplicationDependencies.ksp)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    project(":oboe")
    project(":parselib")

}

//plugins {
//    alias(libs.plugins.androidLibrary)
//    alias(libs.plugins.jetbrainsKotlinAndroid)
//}
//
//android {
//    namespace = "com.slaviboy.iolib"
//    compileSdk = 34
//
//    defaultConfig {
//        minSdk = 21
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//        externalNativeBuild {
//            cmake {
//                cppFlags("")
//                arguments("-DANDROID_STL=c++_shared")
//            }
//        }
//    }
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//    externalNativeBuild {
//        cmake {
//            path("src/main/cpp/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
//    buildFeatures {
//        prefab = true
//    }
//}
//
//dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation(project(":oboe"))
//    implementation(project(":parselib"))
//}