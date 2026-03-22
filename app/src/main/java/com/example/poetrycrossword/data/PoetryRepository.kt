package com.example.poetrycrossword.data

import android.content.Context
import com.example.poetrycrossword.model.Poem
import kotlinx.serialization.json.Json

object PoetryRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun loadPoems(context: Context): List<Poem> {
        val raw = context.assets.open("poems.json").bufferedReader().use { it.readText() }
        return json.decodeFromString(raw)
    }
}
