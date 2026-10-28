package com.yourdomain.freecharades.domain.model

data class Category(
    val id: String,
    val title: String,
    val description: String,
    val imagePath: String,
    val accent: String,
    val words: List<Word>
) {
    val imageUri: String get() = "file:///android_asset/$imagePath"
}
