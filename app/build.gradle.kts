kapt {
    correctErrorTypes = true
}

plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

dependencies {
    implementationPlatform(*ApplicationDependencies.dependenciesPlatformApp)
    implementation(*ApplicationDependencies.dependenciesApp)
    ksp(*ApplicationDependencies.ksp)
    project(":oboe")
    project(":parselib")
    project(":iolib")
    project(":audio")
}