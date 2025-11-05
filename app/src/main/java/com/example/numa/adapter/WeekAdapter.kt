package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.Locale

class WeekAdapter(
    private val weekDays: List<LocalDate>,
    private val onClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {

    private var selectedDate: LocalDate = LocalDate.now()

    inner class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        val dayName: TextView = itemView.findViewById(R.id.tvDayName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_week_day, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val date = weekDays[position]
        holder.dayNumber.text = date.dayOfMonth.toString()
        holder.dayName.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        val isSelected = date == selectedDate
        holder.dayNumber.setBackgroundResource(
            if (isSelected) R.drawable.bg_day_selected else R.drawable.bg_day_unselected
        )

        holder.itemView.setOnClickListener {
            selectedDate = date
            onClick(date)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = weekDays.size
}