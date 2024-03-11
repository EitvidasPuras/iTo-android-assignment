package com.puras.itoandroidassignment.util

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val errorType: ErrorType, val data: T? = null) : Resource<T>()
}

enum class ErrorType {
    NOT_FOUND, UNKNOWN
}