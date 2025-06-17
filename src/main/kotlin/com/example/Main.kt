package com.example

import tornadofx.*
import com.example.UserView
import com.example.Styles
/**
 * Main application class
 */
class MainApp : App(UserView::class, Styles::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}

/**
 * Entry point for the application
 */
fun main() {
    launch<MainApp>()
}
