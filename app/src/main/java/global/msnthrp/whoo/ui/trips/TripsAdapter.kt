package global.msnthrp.whoo.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import global.msnthrp.whoo.databinding.ItemTripBinding
import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.ui.base.BaseAdapter
import global.msnthrp.whoo.ui.utils.DistanceFormatter
import global.msnthrp.whoo.ui.utils.DurationFormatter
import java.text.SimpleDateFormat
import java.util.*


class TripsAdapter : BaseAdapter<Trip, ItemTripBinding>() {

    private val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTripBinding = ItemTripBinding.inflate(inflater, parent, false)

    override fun bind(binding: ItemTripBinding, item: Trip) = with(binding) {
        tvStartTime.text = sdf.format(Date(item.startTime ?: 0L))
        tvDistance.text = DistanceFormatter.format(item.distance, DistanceFormatter.DistanceMetrics.KILOMETERS) + " km"

        val durationMs = (item.endTime ?: 0L) - (item.startTime ?: 0L)
        tvDuration.text = DurationFormatter.format(durationMs, Unit)
    }
}