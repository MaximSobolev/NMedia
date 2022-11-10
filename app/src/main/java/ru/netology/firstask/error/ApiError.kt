package ru.netology.firstask.error

sealed class AppError(var code: String): RuntimeException()
class ApiError(val status: Int, code: String): AppError(code)
class NetworkError : AppError("error_network")
class UnknownError: AppError("error_unknown")