package com.example

import tornadofx.*

class MainApp : App(UserView::class, Styles::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        launch<MainApp>(*args)
    }
}
