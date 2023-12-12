kapt {
    correctErrorTypes = true
}

plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

dependencies {
    implementation(*ApplicationDependencies.dependenciesApp)
    ksp(*ApplicationDependencies.ksp)
    project(":lib:oboe", ":lib:parselib", ":lib:iolib", ":audio")
}