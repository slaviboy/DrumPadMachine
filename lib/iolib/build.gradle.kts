dependencies {
    implementation(*ApplicationDependencies.dependenciesLibraryJNI)
    project(
        ":lib:oboe",
        ":lib:parselib"
    )
}