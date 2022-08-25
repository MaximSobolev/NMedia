package ru.netology.firstask.util

import android.os.Bundle
import ru.netology.firstask.dto.Post
import java.lang.RuntimeException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PostArg : ReadWriteProperty<Bundle, Post?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>): Post? =
        try {
            thisRef.getSerializable(property.name) as Post
        } catch (e : RuntimeException) {
            null
        }


    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Post?) {
        thisRef.putSerializable(property.name, value)
    }
}