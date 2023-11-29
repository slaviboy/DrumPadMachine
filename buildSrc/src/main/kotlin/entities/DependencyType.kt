package entities

sealed class DependencyType {
    object Application : DependencyType()
    object Library : DependencyType()
    object LibraryJNI : DependencyType()
    object Kotlin : DependencyType()
}