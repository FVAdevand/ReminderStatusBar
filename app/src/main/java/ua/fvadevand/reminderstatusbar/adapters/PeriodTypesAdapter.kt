package ua.fvadevand.reminderstatusbar.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ua.fvadevand.reminderstatusbar.data.models.PeriodType

class PeriodTypesAdapter(private val periodTypes: List<Int>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_spinner_item, parent, false)
        val textView = view as? TextView
        textView?.setText(PeriodType.getPeriodTypeStringResId(periodTypes[position]))
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val textView = view as? TextView
        textView?.setText(PeriodType.getPeriodTypeStringResId(periodTypes[position]))
        return view
    }

    override fun getItem(position: Int) = periodTypes[position]

    override fun getItemId(position: Int) = periodTypes[position].toLong()

    override fun getCount() = periodTypes.size
}