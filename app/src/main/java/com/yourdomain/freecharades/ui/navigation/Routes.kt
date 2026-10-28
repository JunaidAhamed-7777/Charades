package com.yourdomain.freecharades.ui.navigation
object Routes {
 const val MENU="menu"
 const val CATEGORY_DETAIL="category/{categoryId}"
 const val GAME="game/{categoryId}/{sessionId}"
 const val SETTINGS="settings"
 const val PRIVACY="privacy"
 const val DOCUMENT="document/{document}"
 const val CREDITS="credits"
 fun categoryDetail(id:String)="category/$id"
 fun game(id:String,session:String)="game/$id/$session"
}
