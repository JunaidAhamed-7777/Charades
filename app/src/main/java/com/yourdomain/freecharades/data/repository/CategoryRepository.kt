package com.yourdomain.freecharades.data.repository

import android.content.Context
import com.yourdomain.freecharades.domain.model.Category
import com.yourdomain.freecharades.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** Reads the complete installed catalog. No network, database, extraction, or content downloads. */
class CategoryRepository(context: Context) {
    private val assets = context.applicationContext.assets
    private val mutex = Mutex()
    @Volatile private var cached: List<Category>? = null

    suspend fun load(): List<Category> = cached ?: mutex.withLock {
        cached ?: withContext(Dispatchers.IO) {
            val document = assets.open("categories/bundled_categories.json")
                .bufferedReader().use { JSONObject(it.readText()) }
            require(document.getInt("version") == 1) { "Unsupported bundled catalog version" }
            val categories = document.getJSONArray("categories")
            val ids = mutableSetOf<String>()
            val wordIds = mutableSetOf<String>()
            val result = List(categories.length()) { categoryIndex ->
                val item = categories.getJSONObject(categoryIndex)
                val id = item.getString("id")
                require(id.matches(Regex("[a-z0-9_]+")) && ids.add(id)) { "Invalid category ID: $id" }
                val words = item.getJSONArray("words")
                require(words.length() > 0) { "Empty category: $id" }
                Category(
                    id = id,
                    title = item.getString("title").also { require(it.isNotBlank()) },
                    description = item.getString("description"),
                    imagePath = checkedImagePath(item.getString("imagePath")),
                    accent = item.optString("accent", "#EC6A55"),
                    words = List(words.length()) { wordIndex ->
                        val word = words.getJSONObject(wordIndex)
                        val wordId = word.getString("id")
                        require(wordId.isNotBlank() && wordIds.add(wordId)) { "Duplicate prompt ID: $wordId" }
                        Word(
                            id = wordId,
                            text = word.getString("text").also { require(it.isNotBlank()) },
                            imagePath = checkedImagePath(word.getString("imagePath")),
                            sourceTitle = word.optString("sourceTitle"),
                            sourceUrl = word.optString("sourceUrl"),
                            credit = word.optString("credit")
                        )
                    }
                )
            }
            require(result.isNotEmpty()) { "The bundled catalog is empty" }
            result.also { cached = it }
        }
    }

    private fun checkedImagePath(path: String): String {
        require(path.startsWith("images/") && !path.contains("..") && !path.contains('\\')) {
            "Images must be bundled asset paths"
        }
        // Check packaging once when loading. An accidentally omitted asset cannot become a blank card.
        assets.open(path).use { require(it.read() != -1) { "Empty photo asset: $path" } }
        return path
    }

    companion object {
        @Volatile private var instance: CategoryRepository? = null
        fun get(context: Context): CategoryRepository = instance ?: synchronized(this) {
            instance ?: CategoryRepository(context).also { instance = it }
        }
    }
}
