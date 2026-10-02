package com.quispe.appnavegation.navigation

sealed class Screen(val route: String) {
    object Login : Screen(route = "login")
    object Home : Screen(route = "home")
    object List : Screen(route = "list")
    object Profile : Screen(route = "profile")

    object Detail : Screen(route = "detail/{itemId}") {
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}
