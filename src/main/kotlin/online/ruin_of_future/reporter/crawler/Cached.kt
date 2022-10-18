package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Cached<T : Any>(
    value: T,
    private val expiredIn: Long
) {
    private var updated: Long = 0
    private var initialized = false
    private val mutex = Mutex()
    var value: T = value
        set(value) {
            runBlocking {
                mutex.withLock {
                    field = value
                    if (initialized) {
                        updated = System.currentTimeMillis()
                    }
                }
            }
        }

    init {
        runBlocking {
            mutex.withLock {
                updated = 0
                initialized = true
            }
        }
    }

    private fun isOutdated(): Boolean = runBlocking {
        mutex.withLock {
            return@runBlocking updated + expiredIn < System.currentTimeMillis()
        }
    }

    fun isNotOutdated(): Boolean {
        return !isOutdated()
    }
}