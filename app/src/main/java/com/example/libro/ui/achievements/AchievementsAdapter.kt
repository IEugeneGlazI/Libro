package com.example.libro.ui.achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.libro.databinding.ItemAchievementBinding
import com.example.libro.R

class AchievementsAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val item = achievements[position]
        with(holder.binding) {
            textAchievementTitle.text = item.title
            textAchievementDesc.text = item.description
            textProgressNumbers.text = "${item.progress} / ${item.goal}"
            progressAchievement.max = item.goal
            progressAchievement.progress = item.progress

            if (item.isCompleted) {
                textAchievementStatus.visibility = View.VISIBLE
                iconAchievementCompleted.visibility = View.VISIBLE
                cardAchievement.strokeColor = ContextCompat.getColor(root.context, R.color.purple_500)
                cardAchievement.strokeWidth = 2
            } else {
                textAchievementStatus.visibility = View.GONE
                iconAchievementCompleted.visibility = View.GONE
                cardAchievement.strokeWidth = 0
            }
        }
    }

    override fun getItemCount() = achievements.size
}
