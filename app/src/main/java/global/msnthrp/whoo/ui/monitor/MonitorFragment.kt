package global.msnthrp.whoo.ui.monitor

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import global.msnthrp.whoo.R
import global.msnthrp.whoo.databinding.FragmentMonitorBinding
import global.msnthrp.whoo.ui.base.BaseFragment
import global.msnthrp.whoo.ui.extensions.applyInsetBottomMargin
import global.msnthrp.whoo.ui.extensions.applyInsetTopMargin
import global.msnthrp.whoo.ui.map.WhooMap
import kotlinx.coroutines.flow.collect


class MonitorFragment : BaseFragment<FragmentMonitorBinding>(R.layout.fragment_monitor) {

    private val viewModel by viewModels<MonitorViewModel>()

    private lateinit var map: WhooMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewBinding(FragmentMonitorBinding.bind(view))
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect(::render)
        }

        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync { mapboxMap ->
                map = WhooMap(mapboxMap)
                map.moveCameraToMyLocation()
            }
            btnTrip.onSwipedListener = viewModel::startStopTrip
            ivMenu.setOnClickListener {
                findNavController().navigate(R.id.action_openTrips)
            }

            ivMenu.applyInsetTopMargin()
            btnTrip.applyInsetBottomMargin()
        }
    }

    private fun render(state: MonitorState) {
        binding.tvSpeed.text = state.speed.speedKmphUi
        binding.tvMaxSpeed.text = state.actualTrip.maxSpeed.speedKmphUi
        binding.tvDistance.text = state.actualTrip.distance.distanceKmUi
        binding.tvAltitude.text = state.altitude.altitudeMUi
        binding.tvMinMaxAltitude.text = state.actualTrip.minMaxAltitude.minMaxAltitudeMUi
        binding.tvSinceStart.text = state.actualTrip.spentTime.spentUi

        val isTripRunning = state.actualTrip.isRunning
        val auxTextColor = when {
            isTripRunning -> R.color.text_high
            else -> R.color.text_low
        }.let { ContextCompat.getColor(requireContext(), it) }
        binding
            .run { listOf(tvMaxSpeed, tvDistance, tvSinceStart, tvMinMaxAltitude) }
            .forEach { it.setTextColor(auxTextColor) }

        if (!::map.isInitialized) return
        map.updateLocPoint(state.locPoint)
        map.updateTrip(state.actualTrip.locPoints)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        super.onDestroyView()
    }

}