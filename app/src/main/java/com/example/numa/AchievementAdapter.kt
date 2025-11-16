package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.databinding.ItemAchievementBinding
import com.example.numa.entity.Achievement

class AchievementAdapter(
    private var achievements: List<Achievement> = emptyList()
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                tvText.text = achievement.title

                // Define ícone baseado no type
                val iconResId = when (achievement.type) {
                    "DAILY_STREAK" -> com.example.numa.R.drawable.ic_fire
                    "HABIT_STREAK" -> com.example.numa.R.drawable.ic_fire
                    "COLECIONADOR" -> com.example.numa.R.drawable.ic_fire
                    "META_CHAMPION" -> com.example.numa.R.drawable.ic_trophy
                    else -> com.example.numa.R.drawable.ic_fire
                }

                ivIcon.setImageResource(iconResId)

                // Define gradiente baseado no type
                val backgroundResId = when (achievement.type) {
                    "DAILY_STREAK" -> com.example.numa.R.drawable.bg_achievement_red
                    "HABIT_STREAK" -> com.example.numa.R.drawable.bg_achievement_purple
                    "COLECIONADOR" -> com.example.numa.R.drawable.bg_achievement_blue
                    "META_CHAMPION" -> com.example.numa.R.drawable.bg_achievement_gold
                    else -> com.example.numa.R.drawable.bg_achievement_red
                }

                // Aplicar background ao LinearLayout (não à CardView)
                root.findViewById<android.view.ViewGroup>(com.example.numa.R.id.linearLayoutAchievement)
                    ?.setBackgroundResource(backgroundResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    fun setAchievements(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}