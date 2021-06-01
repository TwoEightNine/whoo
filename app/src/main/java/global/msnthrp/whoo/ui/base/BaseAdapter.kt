package global.msnthrp.whoo.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseAdapter<T, VB : ViewBinding>() :
    RecyclerView.Adapter<BaseAdapter<T, VB>.BindingViewHolder>() {

    private val items = mutableListOf<T>()

    abstract fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB
    abstract fun bind(binding: VB, item: T)

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        return BindingViewHolder(
            createViewBinding(
                LayoutInflater.from(parent.context),
                parent,
                viewType
            )
        )
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        items.getOrNull(position)
            ?.also { item -> bind(holder.binding, item) }
    }

    fun update(newItems: Collection<T>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class BindingViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}