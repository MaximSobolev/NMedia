package ru.netology.firstask.error

import ru.netology.firstask.dto.Post
import ru.netology.firstask.model.PhotoModel

data class HandlerError (
    val operation: OperationType,
    val argument: Post,
    val error : AppError,
    val photo : PhotoModel? = null
        )