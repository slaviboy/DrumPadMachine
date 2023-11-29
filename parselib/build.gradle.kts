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
}