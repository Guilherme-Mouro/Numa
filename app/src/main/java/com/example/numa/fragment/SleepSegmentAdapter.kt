package com.example.numa.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.databinding.ItemSleepSegmentBinding
import com.example.numa.entity.Sleep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SleepSegmentAdapter(private val sleepSegments: List<Sleep>) : RecyclerView.Adapter<SleepSegmentAdapter.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSleepSegmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val segment = sleepSegments[position]
        holder.bind(segment)
    }

    override fun getItemCount() = sleepSegments.size

    inner class ViewHolder(private val binding: ItemSleepSegmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(segment: Sleep) {
            val startTimeFormatted = timeFormat.format(Date(segment.startTime))
            val endTimeFormatted = timeFormat.format(Date(segment.endTime))

            binding.tvTimeRange.text = "$startTimeFormatted  -  $endTimeFormatted"

            val durationMillis = segment.endTime - segment.startTime
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
            binding.tvSegmentDuration.text = String.format("%dh %02dm", hours, minutes)
        }
    }
}
