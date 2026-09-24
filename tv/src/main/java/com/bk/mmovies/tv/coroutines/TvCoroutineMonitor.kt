package com.bk.mmovies.tv.coroutines

import android.util.Log
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * High-level, observe-only coroutine monitor: logs launches/completions and
 * tracks in-flight coroutines so long-running/leaked ones can be spotted,
 * mirroring [com.bk.mmovies.tv.input.TvInputMonitor]'s event-history shape.
 *
 * Deliberately owns no [CoroutineScope] of its own. [launchMonitored] is an
 * extension on the CALLER's scope (viewModelScope, rememberCoroutineScope,
 * lifecycleScope, ...), so this object never becomes a source of leaks or
 * extra dispatch overhead itself — it just wraps [CoroutineScope.launch] with
 * bookkeeping. Everything is a no-op when [isEnabled] is false.
 */
object TvCoroutineMonitor {
    private const val TAG = "TvCoroutineMonitor"
    private const val MAX_HISTORY = 100

    @Volatile
    var isEnabled: Boolean = true

    sealed interface Event {
        val name: String
        val timeMillis: Long

        data class Started(override val name: String, override val timeMillis: Long) : Event

        data class Completed(
                override val name: String,
                override val timeMillis: Long,
                val durationMillis: Long
                             ) : Event

        data class Cancelled(
                override val name: String,
                override val timeMillis: Long,
                val durationMillis: Long
                             ) : Event

        data class Failed(
                override val name: String,
                override val timeMillis: Long,
                val durationMillis: Long,
                val error: String
                          ) : Event
    }

    data class ActiveCoroutine(val name: String, val job: Job, val startTimeMillis: Long)

    private val history = ArrayDeque<Event>(MAX_HISTORY)
    private val active = ConcurrentHashMap<String, ActiveCoroutine>()

    /**
     * Same as [CoroutineScope.launch], but records a Started event now and a
     * Completed/Cancelled/Failed event when it finishes, and tracks it under
     * [name] while running so it shows up in [activeCoroutines].
     *
     * [name] should be unique enough to identify the call site (e.g.
     * "trailer-prefetch:${item.id}"); reusing the same name for concurrent
     * launches is fine, each gets its own tracking entry keyed by identity.
     */
    fun CoroutineScope.launchMonitored(
            context: CoroutineContext = EmptyCoroutineContext,
            name: String,
            block: suspend CoroutineScope.() -> Unit
                                       ): Job {
        val startTime = System.currentTimeMillis()
        if (isEnabled) record(Event.Started(name, startTime))

        val job = launch(context) { block() }
        val key = "$name@${System.identityHashCode(job)}"
        active[key] = ActiveCoroutine(name, job, startTime)

        job.invokeOnCompletion { cause ->
            active.remove(key)
            if (!isEnabled) return@invokeOnCompletion
            val duration = System.currentTimeMillis() - startTime
            val event = when {
                cause == null                        -> Event.Completed(name, System.currentTimeMillis(), duration)
                cause is CancellationException        -> Event.Cancelled(name, System.currentTimeMillis(), duration)
                else                                  -> Event.Failed(
                        name, System.currentTimeMillis(), duration,
                        cause::class.simpleName + ": " + cause.message
                                                                      )
            }
            record(event)
        }
        return job
    }

    /** Snapshot of coroutines currently tracked as running. */
    fun activeCoroutines(): List<ActiveCoroutine> = active.values.toList()

    /**
     * Active coroutines that have been running longer than [thresholdMillis]
     * as of now — a likely leak or a runaway job, useful to poll from a debug
     * screen or an occasional log dump.
     */
    fun suspectedLeaks(thresholdMillis: Long = 10_000L): List<ActiveCoroutine> {
        val now = System.currentTimeMillis()
        return active.values.filter { now - it.startTimeMillis >= thresholdMillis }
    }

    /** Logs every currently active coroutine and how long it's been running. */
    fun dumpActive() {
        val now = System.currentTimeMillis()
        if (active.isEmpty()) {
            Log.d(TAG, "ACTIVE: none")
            return
        }
        for (entry in active.values) {
            Log.d(TAG, "ACTIVE ${entry.name} running=${now - entry.startTimeMillis}ms job=${entry.job}")
        }
    }

    /** Snapshot of the most recent lifecycle events, oldest first. */
    @Synchronized
    fun recentEvents(): List<Event> = history.toList()

    @Synchronized
    fun clear() = history.clear()

    @Synchronized
    private fun record(event: Event) {
        if (history.size == MAX_HISTORY) history.removeFirst()
        history.addLast(event)
        when (event) {
            is Event.Started   -> Log.d(TAG, "START ${event.name}")
            is Event.Completed -> Log.d(TAG, "DONE ${event.name} in ${event.durationMillis}ms")
            is Event.Cancelled -> Log.d(TAG, "CANCELLED ${event.name} after ${event.durationMillis}ms")
            is Event.Failed    -> Log.w(TAG, "FAILED ${event.name} after ${event.durationMillis}ms: ${event.error}")
        }
    }
}
