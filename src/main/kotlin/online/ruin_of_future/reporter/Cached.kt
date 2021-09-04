package online.ruin_of_future.reporter

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Cached<T : Any>(
    value: T,
    val expiredIn: Long
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

    suspend fun isOutdated(): Boolean {
        mutex.withLock {
            return updated + expiredIn < System.currentTimeMillis()
        }
    }

    suspend fun isNotOutdated(): Boolean {
        return !isOutdated()
    }
}