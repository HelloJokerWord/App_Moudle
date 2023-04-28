package com.third.libcommon.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


val GSON by lazy { Gson() }

fun <T> T.toJson(): String = GSON.toJson(this)

inline fun <reified T> fromJson(json: String): T? {
    return fromJson(json, T::class.java)
}

inline fun <reified T> fromJson(json: String, classOfT: Class<T>): T? {
    return try {
        GSON.fromJson(json, classOfT)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

inline fun <reified T> fromJson(json: String, typeOfT: Type): T? {
    return try {
        GSON.fromJson(json, typeOfT)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type