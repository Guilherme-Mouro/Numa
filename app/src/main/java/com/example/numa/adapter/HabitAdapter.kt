package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import com.example.numa.entity.Habit
import com.google.android.material.card.MaterialCardView
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HabitAdapter(
    val habits: MutableList<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val selectedDate: LocalDate = LocalDate.now() // ✅ Adicionar parâmetro opcional
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tvHabitTitle)
        val duration = itemView.findViewById<TextView>(R.id.tvDuration)
        val streak = itemView.findViewById<TextView>(R.id.tvStreak)
        val xp = itemView.findViewById<TextView>(R.id.tvXp)
        val iconBg = itemView.findViewById<ImageView>(R.id.vIconPlaceholder)
        val layoutXp = itemView.findViewById<LinearLayout>(R.id.layoutXp)
        val layoutDuration = itemView.findViewById<LinearLayout>(R.id.layoutDuration)
        val card = itemView as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.title.text = habit.title
        holder.duration.text = "${(habit.duration / (60 * 1000)).toString()} min"
        holder.streak.text = "${habit.streak} dias"
        holder.xp.text = "+${habit.experience} XP"

        // ✅ Usar a data selecionada, não LocalDate.now()
        val selectedDayStart = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
        val selectedDayEnd = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

        val isCompletedToday = if (habit.isRecurring) {
            // ✅ Verifica se foi completado no dia selecionado
            habit.lastCompletedDate >= selectedDayStart && habit.lastCompletedDate < selectedDayEnd
        } else {
            // Para hábitos de data específica, usa o campo state
            habit.state == "complete"
        }

        // ✅ Aplicar a aparência baseado em isCompletedToday
        if (isCompletedToday) {
            holder.card.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.green)
            holder.card.strokeWidth = 15

            holder.title.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

            holder.iconBg.setBackgroundResource(R.drawable.ic_completed_background)
            holder.iconBg.setImageResource(R.drawable.ic_check)
            holder.iconBg.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))

            holder.layoutXp.visibility = View.GONE
            holder.layoutDuration.visibility = View.GONE
        } else {
            // ✅ Reset da aparência para hábitos não completados
            holder.card.strokeWidth = 0
            holder.title.paintFlags = 0 // Remove o strikethrough

            // Reset do ícone (se necessário, ajusta para o ícone padrão)
            holder.iconBg.setBackgroundResource(0) // ou o drawable padrão
            holder.iconBg.setImageResource(0) // ou o ícone padrão

            holder.layoutXp.visibility = View.VISIBLE
            holder.layoutDuration.visibility = View.VISIBLE
        }

        //Handles habit click
        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }
    }

    override fun getItemCount() = habits.size

    fun removeItem(position: Int) {
        habits.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getHabitAt(position: Int): Habit {
        return habits[position]
    }
}