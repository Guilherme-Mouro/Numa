package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R

// Data class para representar cada quest
data class Quest(
    val title: String,
    val progress: Int  // 0-100
)

class ProgressQuestAdapter(
    private val quests: List<Quest>
) : RecyclerView.Adapter<ProgressQuestAdapter.QuestViewHolder>() {

    inner class QuestViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val habitTitle: TextView = itemView.findViewById(R.id.habitTitle)
        private val habitPercentage: TextView = itemView.findViewById(R.id.habitPercentage)
        private val habitProgress: ProgressBar = itemView.findViewById(R.id.habitProgress)

        fun bind(quest: Quest) {
            habitTitle.text = quest.title
            habitPercentage.text = "${quest.progress}%"
            habitProgress.progress = quest.progress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(quests[position])
    }

    override fun getItemCount(): Int = quests.size
}