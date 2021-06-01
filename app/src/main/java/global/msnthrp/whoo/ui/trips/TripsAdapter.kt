package global.msnthrp.whoo.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import global.msnthrp.whoo.databinding.ItemTripBinding
import global.msnthrp.whoo.domain.Trip
import global.msnthrp.whoo.ui.base.BaseAdapter


class TripsAdapter : BaseAdapter<Trip, ItemTripBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTripBinding = ItemTripBinding.inflate(inflater, parent, false)

    override fun bind(binding: ItemTripBinding, item: Trip) = with(binding) {
        tvTrip.text = item.toString()
    }
}