package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import com.example.numa.entity.Habit

class HabitAdapter(private var habits: List<Habit>): RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvHabitTitle)
        val duration = itemView.findViewById<TextView>(R.id.tvDuration)
        val streak = itemView.findViewById<TextView>(R.id.tvStreak)
        val xp = itemView.findViewById<TextView>(R.id.tvXp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.title.text = habit.title
        holder.duration.text = habit.duration.toString()
        holder.streak.text = "${habit.streak} dias"
        holder.xp.text = "+${habit.experience} XP"
    }

    override fun getItemCount() = habits.size

}