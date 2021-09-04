package online.ruin_of_future.reporter

class Cached<T : Any>(
    value: T,
    val expiredIn: Long
) {
    private var updated: Long = 0
    private var initialized = false
    var value: T = value
        set(value) {
            field = value
            if (initialized) {
                updated = System.currentTimeMillis()
            }
        }

    init {
        updated = 0
        initialized = true
    }

    fun isOutdated(): Boolean {
        return updated + expiredIn < System.currentTimeMillis()
    }

    fun isNotOutdated(): Boolean {
        return !isOutdated()
    }
}