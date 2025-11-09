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
        val achievements = listOf(
            Achievement("Первый читатель", "Прочитайте первую книгу", 1, 1, true),
            Achievement("Марафонец", "Прочитайте 5 книг", 2, 5, false),
            Achievement("Книжный червь", "Прочитайте 10 книг", 2, 10, false),
            Achievement("Мастер чтения", "Прочитайте 25 книг", 2, 25, false)
        )

        binding.textAchievementsSummary.text = "Получено: 1 из ${achievements.size}"

        val adapter = AchievementsAdapter(achievements)
        binding.recyclerAchievements.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAchievements.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
