package com.yourdomain.freecharades.ui.navigation

object Routes {
    const val MENU = "menu"
    const val CATEGORY_DETAIL = "category/{categoryId}"
    const val GAME = "game/{categoryId}"
    const val SETTINGS = "settings"
    const val PRIVACY = "privacy"

    fun categoryDetail(categoryId: String) = "category/$categoryId"
    fun game(categoryId: String) = "game/$categoryId"
}