package global.msnthrp.whoo.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


abstract class BaseViewModel<S> : ViewModel() {

    val uiState: StateFlow<S>
        get() = uiStateFlow

    private lateinit var uiStateFlow: MutableStateFlow<S>

    abstract fun createInitialState(): S

    protected fun initState() {
        val initialState = createInitialState()
        uiStateFlow = MutableStateFlow(initialState)
    }

    protected fun updateState(updateBlock: (S) -> S) {
        uiStateFlow.value = uiStateFlow.value.let(updateBlock)
    }
}