package com.example.libro.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAchievements()
    }

    override fun onResume() {
        super.onResume()
        loadAchievements()
    }

    private fun loadAchievements() {
        val achievements = AchievementCalculator.calculateAllAchievements(requireContext())
        
        val completedCount = achievements.count { it.isCompleted }
        binding.textAchievementsSummary.text = "Получено: $completedCount из ${achievements.size}"

        val adapter = AchievementsAdapter(achievements)
        binding.recyclerAchievements.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAchievements.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
