package com.yourdomain.freecharades.domain.model

/** A photo prompt shipped inside the APK. Paths are relative to the assets directory. */
data class Word(
    val id: String,
    val text: String,
    val imagePath: String,
    val sourceTitle: String = "",
    val sourceUrl: String = "",
    val credit: String = ""
) {
    val imageUri: String get() = "file:///android_asset/$imagePath"
}
