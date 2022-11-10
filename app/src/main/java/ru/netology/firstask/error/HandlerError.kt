package ru.netology.firstask.error

import ru.netology.firstask.dto.Post

data class HandlerError (
    val operation: OperationType,
    val argument: Post,
    val error : AppError
        )