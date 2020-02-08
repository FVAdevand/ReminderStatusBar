package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

import ua.fvadevand.reminderstatusbar.R

class IconAdapter(
    private val iconIds: List<Int>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(iconIds[position])
    }

    override fun getItemCount(): Int {
        return iconIds.size
    }

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iconView: ImageView = itemView.findViewById(R.id.iv_icon)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position in 0 until itemCount) {
                    listener(iconIds[position])
                }
            }
        }

        fun bind(@DrawableRes iconId: Int) {
            iconView.setImageResource(iconId)
        }
    }
}
