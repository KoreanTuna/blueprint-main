package com.medithings.blueprint.support

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

//https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
//https://gist.github.com/JoseAlcerreca/5b661f1800e1e654f07cc54fe87441af
open class Event<T>(private val content: T?) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T? = content

    companion object {

        fun <T> from(originLiveData: LiveData<T>) =
            Transformations.map(originLiveData) { Event(it) }
    }
}

// Event는 단일 observing만 가능함을 명심할 것
inline fun <T> LiveData<out Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    observe(owner, Observer {
        it?.getContentIfNotHandled()?.let(onEventUnhandledContent)
    })
}

inline fun <T> LiveData<out Event<T>>.observeEventOnce(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    observe(owner, object : Observer<Event<T>?> {
        override fun onChanged(t: Event<T>?) {
            t?.getContentIfNotHandled()?.let {
                onEventUnhandledContent(it)
                removeObserver(this)
            }
        }
    })
}