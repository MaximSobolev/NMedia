package ru.netology.firstask.util

import android.os.Bundle
import ru.netology.firstask.dto.DraftPost
import java.lang.RuntimeException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object DraftPostArg : ReadWriteProperty<Bundle, DraftPost?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>): DraftPost? =
        try {
            thisRef.getSerializable(property.name) as DraftPost
        } catch (e : RuntimeException) {
            null
        }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: DraftPost?) {
        thisRef.putSerializable(property.name, value)
    }
}