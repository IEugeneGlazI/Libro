package com.example.libro.ui.achievements

import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
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

            // Устанавливаем иконку достижения
            iconAchievement.setImageResource(item.iconResId)

            // Устанавливаем цвет прогресс-бара
            val progressColor = ContextCompat.getColor(root.context, item.progressColorResId)
            progressAchievement.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)

            // Устанавливаем цвет иконки (совпадает с цветом прогресс-бара)
            iconAchievement.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN)

            // Устанавливаем цвет фона иконки (светлее чем цвет прогресс-бара)
            val iconBackgroundColor = ContextCompat.getColor(root.context, item.iconBackgroundColorResId)
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 10f * root.context.resources.displayMetrics.density
                setColor(iconBackgroundColor)
            }
            iconAchievement.background = backgroundDrawable

            if (item.isCompleted) {
                textAchievementStatus.visibility = View.VISIBLE
                iconAchievementCompleted.visibility = View.VISIBLE
                cardAchievement.strokeColor = ContextCompat.getColor(root.context, R.color.achievement_completed_border)
                cardAchievement.strokeWidth = 2
                // Устанавливаем цвет счетчика для выполненных достижений
                textProgressNumbers.setTextColor(ContextCompat.getColor(root.context, R.color.purple_500))
            } else {
                textAchievementStatus.visibility = View.GONE
                iconAchievementCompleted.visibility = View.GONE
                cardAchievement.strokeWidth = 0
                // Обычный цвет для невыполненных достижений
                textProgressNumbers.setTextColor(ContextCompat.getColor(root.context, R.color.black))
            }
        }
    }

    override fun getItemCount() = achievements.size
}
