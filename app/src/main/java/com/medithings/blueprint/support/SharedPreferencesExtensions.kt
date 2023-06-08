package com.medithings.blueprint.support

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(
    defaultValue: T,
    key: String?,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
            edit().setter(key ?: property.name, value).apply()
    }
}

fun SharedPreferences.int(def: Int = 0, key: String? = null) =
    delegate(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.long(def: Long = 0, key: String? = null) =
    delegate(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

fun SharedPreferences.float(def: Float = 0f, key: String? = null) =
    delegate(def, key, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)

fun SharedPreferences.boolean(
    def: Boolean = false,
    key: String? = null
) = delegate(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.stringSet(
    def: Set<String> = emptySet(),
    key: String? = null
) = delegate(def, key, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet)

fun SharedPreferences.string(
    def: String = "",
    key: String? = null
) = delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)
