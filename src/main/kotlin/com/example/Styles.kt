package com.example

import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Styles for the application
 */
class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val mainColor = c("#3777bf")
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            textFill = mainColor
        }
        
        button {
            backgroundColor += mainColor
            textFill = c("white")
            padding = box(8.px, 15.px)
            fontWeight = FontWeight.BOLD
        }
        
        tableView {
            prefHeight = 500.px
        }
    }
}
