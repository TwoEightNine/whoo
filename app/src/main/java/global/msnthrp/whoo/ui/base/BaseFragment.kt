package global.msnthrp.whoo.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException


abstract class BaseFragment<VB : ViewBinding>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var bindingOrNull: VB? = null

    val binding: VB
        get() = bindingOrNull ?: throw IllegalStateException("ViewBinding is not yet attached or already detached!")

    protected fun setViewBinding(viewBinding: VB) {
        bindingOrNull = viewBinding
    }

    override fun onDestroyView() {
        bindingOrNull = null
        super.onDestroyView()
    }
}