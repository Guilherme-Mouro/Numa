package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import com.example.numa.databinding.ItemAchievementBinding
import com.example.numa.entity.Achievement

class AchievementAdapter(

    private var achievements: List<Achievement> = emptyList(),
    private val clickListener: (Achievement) -> Unit
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                tvText.text = achievement.title

                // 1. Define Ícone baseado no type
                val iconResId = when (achievement.type) {
                    "DAILY_STREAK" -> R.drawable.ic_fire
                    "HABIT_STREAK" -> R.drawable.ic_fire
                    "COLECIONADOR" -> R.drawable.ic_collection
                    "META_CHAMPION" -> R.drawable.ic_trophy
                    else -> R.drawable.ic_fire
                }
                ivIcon.setImageResource(iconResId)

                // 2. Define Cor do Ícone baseado no type
                val iconColorResId = when (achievement.type) {
                    "DAILY_STREAK" -> R.color.red_icon // Certifique-se de que este recurso de cor existe
                    "HABIT_STREAK" -> R.color.purple_icon // Certifique-se de que este recurso de cor existe
                    "COLECIONADOR" -> R.color.blue_icon // Certifique-se de que este recurso de cor existe
                    "META_CHAMPION" -> R.color.gold_icon // Certifique-se de que este recurso de cor existe
                    else -> R.color.default_icon_color // Cor padrão
                }

                // 3. Aplica o filtro de cor ao ícone
                val iconColor = ContextCompat.getColor(root.context, iconColorResId)
                ivIcon.setColorFilter(iconColor)


                // 4. Define Gradiente (Background) baseado no type (Lógica original)
                val backgroundResId = when (achievement.type) {
                    "DAILY_STREAK" -> R.drawable.bg_achievement_red
                    "HABIT_STREAK" -> R.drawable.bg_achievement_purple
                    "COLECIONADOR" -> R.drawable.bg_achievement_blue
                    "META_CHAMPION" -> R.drawable.bg_achievement_gold
                    else -> R.drawable.bg_achievement_red
                }

                // Aplicar background ao LinearLayout
                root.findViewById<ViewGroup>(R.id.linearLayoutAchievement)
                    ?.setBackgroundResource(backgroundResId)
            }
            binding.root.setOnClickListener {
                clickListener(achievement)
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