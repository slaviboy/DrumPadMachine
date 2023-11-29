package entities

sealed class Dependency(
    open val path: String,
    open val name: String,
    open val type: DependencyType
) {
    sealed class LibraryDependency(
        override val path: String,
        override val name: String
    ) : Dependency(path, name, DependencyType.Library)

    sealed class LibraryJNIDependency(
        override val path: String,
        override val name: String
    ) : Dependency(path, name, DependencyType.LibraryJNI)

    object App : Dependency(":app", "app", DependencyType.Application)
    object Audio : LibraryDependency(":audio", "audio")
    object Oboe : LibraryJNIDependency(":oboe", "oboe")
    object IOLib : LibraryJNIDependency(":iolib", "iolib")
    object ParseLib : LibraryJNIDependency(":parselib", "parselib")

    fun matches(project: org.gradle.api.Project): Boolean {
        return (project.path == path && project.name == name)
    }
}