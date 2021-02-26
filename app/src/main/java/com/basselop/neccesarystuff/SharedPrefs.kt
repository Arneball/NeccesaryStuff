package com.basselop.neccesarystuff

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Serializer {
    fun <T> parse(str: String, tClass: Class<T>): T
    fun <T> toJson(t: T): String
}

inline fun SharedPreferences.editor(crossinline f: SharedPreferences.Editor.() -> Unit) {
    edit().also(f).apply()
}

inline fun <T : Any?> SharedPreferences.prefsRw(
    default: T,
    crossinline get: SharedPreferences.(String, T) -> T,
    crossinline set: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
    name: String? = null // added this name so that when you rename because you're lazy and dont want to migrate: here is the remedy.
) = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        get(name ?: property.name, default)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        editor { set(name ?: property.name, value) }
    }
}

fun SharedPreferences.boolPref(default: Boolean = false) = prefsRw(
    default = default,
    get = SharedPreferences::getBoolean,
    set = SharedPreferences.Editor::putBoolean
)

inline fun <reified T : Any> SharedPreferences.jsonPref(
    serializer: Serializer,
    default: T,
    name: String? = null
) = prefsRw(
    default = default,
    get = { k, _ ->
        when (val str = getString(k, null)) {
            null -> default
            else -> runCatching {
                serializer.parse(str, T::class.java)
            }.getOrDefault(default)
        }
    },
    set = { k, t ->
        putString(k, serializer.toJson(t))
        this
    },
    name = name,
)

fun SharedPreferences.stringPref(default: String? = null, name: String? = null) = prefsRw(
    default,
    SharedPreferences::getString,
    SharedPreferences.Editor::putString,
    name
)

fun SharedPreferences.intPref(default: Int) = prefsRw(
    default, SharedPreferences::getInt, SharedPreferences.Editor::putInt
)

fun SharedPreferences.longPref(default: Long, name: String?) = prefsRw(
    default, SharedPreferences::getLong, SharedPreferences.Editor::putLong, name
)

fun SharedPreferences.floatPref(default: Float, name: String? = null) = prefsRw(
    default, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat, name,
)