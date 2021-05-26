package global.msnthrp.whoo.ui.trips

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import global.msnthrp.whoo.R
import global.msnthrp.whoo.databinding.FragmentTripsBinding
import global.msnthrp.whoo.ui.base.BaseFragment
import global.msnthrp.whoo.ui.extensions.applyInsetBottomPadding
import global.msnthrp.whoo.ui.extensions.applyInsetTopPadding
import kotlinx.coroutines.flow.collect


class TripsFragment : BaseFragment<FragmentTripsBinding>(R.layout.fragment_trips) {

    private val viewModel by viewModels<TripsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewBinding(FragmentTripsBinding.bind(view))
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect(::render)
        }

        binding.apply {
            toolbar.applyInsetTopPadding()
            rvTrips.applyInsetBottomPadding()
        }
    }

    private fun render(state: TripsState) {

    }
}