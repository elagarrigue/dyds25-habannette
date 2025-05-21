package edu.dyds.movies

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
// Rama creada, comentario de prueba
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DYDSProject",
    ) {
        App()
    }
}