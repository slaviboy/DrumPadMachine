dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(*ApplicationDependencies.dependenciesLibraryJNI)
    project(":oboe")
}