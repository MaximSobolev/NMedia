package ru.netology.firstask.dto

import ru.netology.firstask.model.PhotoModel
import java.io.Serializable

data class DraftPost (
    val content : String,
    val photo : PhotoModel?) : Serializable