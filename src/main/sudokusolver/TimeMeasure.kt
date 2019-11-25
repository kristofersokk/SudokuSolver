package sudokusolver

class TimeMeasure {
    private var start: Long = 0
    private var finish: Long = 0
    fun start() {
        start = System.nanoTime()
    }

    fun stop() {
        finish = System.nanoTime()
    }

    /**
     * @return measured operation duration in nanoseconds
     */
    private val duration: Long
        get() = finish - start

    val durationString: String
        get() {
            var result = ""
            var duration = duration
            if (duration > 60000000000L) {
                result += (duration / 60000000000L).toString() + "min "
                duration %= 60000000000L
            }
            if (duration > 1000000000L) {
                result += (duration / 1000000000L).toString() + "s "
                duration %= 1000000000L
            }
            result += String.format("%.2f", duration / 1000000.0).replace(",", ".") + "ms"
            return result
        }
}