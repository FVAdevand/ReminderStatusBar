package ua.fvadevand.reminderstatusbar.utils

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentPropertyWrapper(
    lifecycleOwner: LifecycleOwner,
    viewLifecycleOwnerLiveData: LiveData<LifecycleOwner>
) {

    private val delegateViewLifecycleList = mutableListOf<Resetable>()
    private val delegateFragmentLifecycleList = mutableListOf<Resetable>()

    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                mainHandler.post {
                    delegateFragmentLifecycleList.forEach(Resetable::reset)
                }
            }
        })
        val viewLifecycleObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                mainHandler.post {
                    delegateViewLifecycleList.forEach(Resetable::reset)
                }
            }
        }
        viewLifecycleOwnerLiveData.observe(lifecycleOwner) {
            it.lifecycle.addObserver(viewLifecycleObserver)
        }
    }

    fun <T> delegateViewLifecycle(): ReadWriteProperty<Fragment, T?> {
        val delegate = PropertyLifecycleDelegate<T>()
        delegateViewLifecycleList.add(delegate)
        return delegate
    }

    fun <T> lateinitDelegateViewLifecycle(): ReadWriteProperty<Fragment, T> {
        val delegate = LateinitPropertyLifecycleDelegate<T>()
        delegateViewLifecycleList.add(delegate)
        return delegate
    }

    fun <T> delegateFragmentLifecycle(): ReadWriteProperty<Fragment, T?> {
        val delegate = PropertyLifecycleDelegate<T>()
        delegateFragmentLifecycleList.add(delegate)
        return delegate
    }

    fun <T : ViewBinding> fragmentLateinitViewBindingByView(viewBinder: (View) -> T): ReadOnlyProperty<Fragment, T> {
        val delegate = LateinitBindingLifecycleDelegateByView(viewBinder)
        delegateViewLifecycleList.add(delegate)
        return delegate
    }

    fun <T : ViewBinding> fragmentLateinitViewBindingByInflater(viewBinder: (LayoutInflater) -> T): ReadOnlyProperty<Fragment, T> {
        val delegate = LateinitBindingLifecycleDelegateByInflater(viewBinder)
        delegateViewLifecycleList.add(delegate)
        return delegate
    }

    private class PropertyLifecycleDelegate<T> : ReadWriteProperty<Fragment, T?>, Resetable {

        private var prop: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
            return prop
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
            prop = value
        }

        override fun reset() {
            prop = null
        }

    }

    private class LateinitPropertyLifecycleDelegate<T> : ReadWriteProperty<Fragment, T>, Resetable {

        private var prop: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return prop ?: throw IllegalStateException(
                "Can't access the Fragment property when getView() is null i.e., before onCreateView() or after onDestroyView()"
            )
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            prop = value
        }

        override fun reset() {
            prop = null
        }

    }

    private class LateinitBindingLifecycleDelegateByView<T : ViewBinding>(private val viewBinder: (View) -> T) :
        ReadOnlyProperty<Fragment, T>, Resetable {

        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return binding ?: viewBinder.invoke(thisRef.requireView()).also { binding = it }
        }

        override fun reset() {
            binding = null
        }

    }

    private class LateinitBindingLifecycleDelegateByInflater<T : ViewBinding>(private val viewBinder: (LayoutInflater) -> T) :
        ReadOnlyProperty<Fragment, T>, Resetable {

        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return binding ?: viewBinder.invoke(LayoutInflater.from(thisRef.requireContext()))
                .also { binding = it }
        }

        override fun reset() {
            binding = null
        }

    }

    private interface Resetable {
        fun reset()
    }

}

fun Fragment.fragmentProperty(): Lazy<FragmentPropertyWrapper> {
    var cached: FragmentPropertyWrapper? = null
    return object : Lazy<FragmentPropertyWrapper> {
        override val value: FragmentPropertyWrapper
            get() {
                return cached ?: FragmentPropertyWrapper(
                    this@fragmentProperty,
                    this@fragmentProperty.viewLifecycleOwnerLiveData
                ).also {
                    cached = it
                }
            }

        override fun isInitialized() = cached != null
    }
}