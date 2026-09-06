package com.pinwave.data.pinterest

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Client-side pacing for Pinterest API calls (spec §19 rate limits).
 * Conservative fixed interval; real per-endpoint quota headers land with the
 * live integration in roadmap step 6.
 */
@Singleton
class PinterestRateLimiter @Inject constructor() {
    private val mutex = Mutex()
    private var lastCallAt = 0L

    suspend fun <T> throttle(block: suspend () -> T): T {
        mutex.withLock {
            val wait = MIN_INTERVAL_MS - (System.currentTimeMillis() - lastCallAt)
            if (wait > 0) delay(wait)
            lastCallAt = System.currentTimeMillis()
        }
        return block()
    }

    private companion object {
        const val MIN_INTERVAL_MS = 350L
    }
}
