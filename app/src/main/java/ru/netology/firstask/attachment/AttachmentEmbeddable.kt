package ru.netology.firstask.attachment

data class AttachmentEmbeddable (
    var url : String,
    var description: String?,
    var type: AttachmentType
)