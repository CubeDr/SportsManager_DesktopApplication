import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                postOperation(operation())
                delay(delayMillis)
            }
        }
    }

    fun stop() = job?.cancel()
}