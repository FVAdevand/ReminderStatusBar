package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.databinding.ListItemIconBinding

class IconAdapter(
    private val iconIds: List<Int>,
    private val onClickListener: (iconId: Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val itemBinding = ListItemIconBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IconViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(iconIds[position])
    }

    override fun getItemCount(): Int {
        return iconIds.size
    }

    inner class IconViewHolder(
        private val itemBinding: ListItemIconBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position in 0 until itemCount) {
                    onClickListener(iconIds[position])
                }
            }
        }

        fun bind(@DrawableRes iconId: Int) {
            itemBinding.ivIcon.setImageResource(iconId)
        }
    }
}
