package sportsmanager

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Utils<T>(
    private val delayMillis: Long = 5000,
    private val operation: () -> T,
    private val postOperation: (T) -> Unit)
{
    private var job: Job? = null

    fun start() {
        stop()
        job = GlobalScope.launch {
            while(true) {
                postOperation(operation())
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