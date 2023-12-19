package com.slaviboy.drumpadmachine.api.results

sealed class Result<out T> {

    object Initial : Result<Nothing>()
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Fail(val errorMessage: String) : Result<Nothing>()

    fun isLoadingOrSuccess(): Boolean {
        return (this is Loading || this is Success)
    }
}