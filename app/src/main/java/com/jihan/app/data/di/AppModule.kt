package com.jihan.app.data.di

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.converter.kotlinx.serialization.asConverterFactory


private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
    prettyPrint = true
    coerceInputValues = true
}

val appModule = module {
    val jsonConverterFactory = json.asConverterFactory("application/json; charset=UTF-8".toMediaType())



}


