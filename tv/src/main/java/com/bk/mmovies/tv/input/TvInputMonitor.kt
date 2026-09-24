package com.bk.mmovies.tv.input

import android.util.Log
import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Central place that monitors and logs the app's remote/keyboard key presses
 * and Compose focus changes.
 *
 * - Key events: call [onKeyEvent] from `Activity.dispatchKeyEvent` (see
 *   TvMainActivity); it only observes and never consumes.
 * - Focus: attach [Modifier.monitorFocus] to any focusable composable.
 *
 * Everything is a no-op when [isEnabled] is false. Recent events are kept in a
 * bounded in-memory history ([recentEvents]) for quick dumps while debugging.
 */
object TvInputMonitor {
    private const val TAG = "TvInputMonitor"
    private const val MAX_HISTORY = 100

    @Volatile
    var isEnabled: Boolean = true

    sealed interface Event {
        val timeMillis: Long

        data class Key(
                val keyName: String,
                val action: String,
                val repeatCount: Int,
                override val timeMillis: Long
                      ) : Event

        data class Focus(
                val target: String,
                val isFocused: Boolean,
                override val timeMillis: Long
                        ) : Event
    }

    private val history = ArrayDeque<Event>(MAX_HISTORY)

    /** Return true to consume the key so nothing else in the app sees it. */
    fun interface KeyInterceptor {
        fun onKeyEvent(event: KeyEvent): Boolean
    }

    private val interceptors = CopyOnWriteArrayList<KeyInterceptor>()
    private val remaps = ConcurrentHashMap<Int, Int>()

    /** Newest interceptor runs first. Returns a handle that unregisters it. */
    fun addInterceptor(interceptor: KeyInterceptor): () -> Unit {
        interceptors.add(0, interceptor)
        return { interceptors.remove(interceptor) }
    }

    /** Every [fromKeyCode] event reaches the app as [toKeyCode] instead. */
    fun remapKey(fromKeyCode: Int, toKeyCode: Int) {
        remaps[fromKeyCode] = toKeyCode
    }

    fun clearRemap(fromKeyCode: Int) {
        remaps.remove(fromKeyCode)
    }

    /**
     * Single entry point for TvMainActivity.dispatchKeyEvent: logs the key,
     * applies remaps, then offers it to the interceptors. Returns the event
     * the app should handle, or null if an interceptor consumed it. With no
     * remaps/interceptors registered this returns [event] unchanged.
     */
    fun process(event: KeyEvent): KeyEvent? {
        onKeyEvent(event)
        val effective = remaps[event.keyCode]?.let { event.withKeyCode(it) } ?: event
        if (effective !== event && isEnabled) {
            Log.d(TAG, "REMAP ${KeyEvent.keyCodeToString(event.keyCode)} -> ${KeyEvent.keyCodeToString(effective.keyCode)}")
        }
        for (interceptor in interceptors) {
            if (interceptor.onKeyEvent(effective)) {
                if (isEnabled) Log.d(TAG, "CONSUMED ${KeyEvent.keyCodeToString(effective.keyCode)}")
                return null
            }
        }
        return effective
    }

    private fun KeyEvent.withKeyCode(newKeyCode: Int) = KeyEvent(
            downTime, eventTime, action, newKeyCode, repeatCount, metaState,
            deviceId, scanCode, flags, source
                                                                )

    fun onKeyEvent(event: KeyEvent) {
        if (!isEnabled) return
        val action = when (event.action) {
            KeyEvent.ACTION_DOWN -> "DOWN"
            KeyEvent.ACTION_UP   -> "UP"
            else                 -> "OTHER(${event.action})"
        }
        record(
                Event.Key(
                        keyName = KeyEvent.keyCodeToString(event.keyCode),
                        action = action,
                        repeatCount = event.repeatCount,
                        timeMillis = System.currentTimeMillis()
                         )
              )
    }

    private val focusTargets = ConcurrentHashMap<String, FocusRequester>()
    private val nextFocusOverrides = ConcurrentHashMap<Pair<String, FocusDirection>, String>()

    internal fun registerFocusTarget(name: String, requester: FocusRequester) {
        focusTargets[name] = requester
    }

    internal fun unregisterFocusTarget(name: String, requester: FocusRequester) {
        focusTargets.remove(name, requester)
    }

    /**
     * Makes a D-pad [direction] move from the element named [from] land on
     * the element named [to] (both tagged with Modifier.monitorFocus). If
     * [to] isn't composed at that moment, default focus search is used.
     */
    fun setNextFocus(from: String, direction: FocusDirection, to: String) {
        nextFocusOverrides[from to direction] = to
        if (isEnabled) Log.d(TAG, "NEXT_FOCUS $from $direction -> $to")
    }

    fun clearNextFocus(from: String, direction: FocusDirection) {
        nextFocusOverrides.remove(from to direction)
    }

    /** Moves focus to the element named [name]; false if it isn't composed or can't take focus. */
    fun requestFocus(name: String): Boolean {
        val requester = focusTargets[name] ?: return false
        return runCatching { requester.requestFocus() }.getOrDefault(false)
    }

    // Read at focus-search time by the focusProperties in monitorFocus.
    internal fun nextFocusFor(from: String, direction: FocusDirection): FocusRequester {
        val to = nextFocusOverrides[from to direction] ?: return FocusRequester.Default
        return focusTargets[to] ?: FocusRequester.Default
    }

    fun onFocusChanged(target: String, isFocused: Boolean) {
        if (!isEnabled) return
        record(Event.Focus(target, isFocused, System.currentTimeMillis()))
    }

    /** Snapshot of the most recent events, oldest first. */
    @Synchronized
    fun recentEvents(): List<Event> = history.toList()

    @Synchronized
    fun clear() = history.clear()

    @Synchronized
    private fun record(event: Event) {
        if (history.size == MAX_HISTORY) history.removeFirst()
        history.addLast(event)
        when (event) {
            is Event.Key   -> Log.d(TAG, "KEY ${event.keyName} ${event.action} repeat=${event.repeatCount}")
            is Event.Focus -> Log.d(TAG, "FOCUS ${event.target} ${if (event.isFocused) "gained" else "lost"}")
        }
    }
}

/**
 * Registers [interceptor] while this composable is in the composition (e.g. a
 * screen-scoped BACK rule) and unregisters it when it leaves.
 */
@Composable
fun KeyInterceptorEffect(interceptor: TvInputMonitor.KeyInterceptor) {
    val latest by rememberUpdatedState(interceptor)
    DisposableEffect(Unit) {
        val remove = TvInputMonitor.addInterceptor { latest.onKeyEvent(it) }
        onDispose { remove() }
    }
}

/**
 * Names this element [name] with [TvInputMonitor]: logs focus gained/lost,
 * makes it a target for [TvInputMonitor.requestFocus], and lets
 * [TvInputMonitor.setNextFocus] override where D-pad moves go from it.
 *
 * Like onFocusChanged/focusProperties, it applies to the focus target that
 * FOLLOWS it in the chain, so place it before .clickable/.focusable.
 */
fun Modifier.monitorFocus(name: String): Modifier = composed {
    val requester = remember(name) { FocusRequester() }
    DisposableEffect(name, requester) {
        TvInputMonitor.registerFocusTarget(name, requester)
        onDispose { TvInputMonitor.unregisterFocusTarget(name, requester) }
    }
    Modifier
            .focusRequester(requester)
            .focusProperties {
                up = TvInputMonitor.nextFocusFor(name, FocusDirection.Up)
                down = TvInputMonitor.nextFocusFor(name, FocusDirection.Down)
                left = TvInputMonitor.nextFocusFor(name, FocusDirection.Left)
                right = TvInputMonitor.nextFocusFor(name, FocusDirection.Right)
            }
            .onFocusChanged { TvInputMonitor.onFocusChanged(name, it.isFocused) }
}
