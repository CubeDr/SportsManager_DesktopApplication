package sportsmanager

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.move
import tornadofx.select
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Repeat<T>(
    private val delayMillis: Long = 5000,
    private val operation: () -> T,
    private val postOperation: (T) -> Unit)
{
    private var job: Job? = null

    fun start() {
        stop()
        job = GlobalScope.launch {
            while(true) {
                val result = operation()
                GlobalScope.launch(Dispatchers.JavaFx) {
                    postOperation(result)
                }
                delay(delayMillis)
            }
        }
    }

    fun stop() = job?.cancel()
}

inline fun <T:Any, R> T?.notNull(callback: (T)->R): R? {
    return this?.let(callback)
}

fun LocalDate.toString(format: String): String = this.format(DateTimeFormatter.ofPattern(format))

fun TabPane.addToLast(tab: Tab, select: Boolean = true) {
    tabs.add(tab)
    tabs.move(tab, tabs.size-2)
    if(select) tab.select()
}

fun Node.topAnchor(value: Double) = AnchorPane.setTopAnchor(this, value)
fun Node.bottomAnchor(value: Double) = AnchorPane.setBottomAnchor(this, value)
fun Node.leftAnchor(value: Double) = AnchorPane.setLeftAnchor(this, value)
fun Node.rightAnchor(value: Double) = AnchorPane.setRightAnchor(this, value)

fun label(text: String = "", color: Paint = Color.BLACK, size: Double = 10.0) = Label(text).apply {
    textFill = color
    font = Font(size)
}