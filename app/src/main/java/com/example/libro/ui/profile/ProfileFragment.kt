package com.example.libro.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.libro.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupProfileInfo()
        setupSwitches()
        setupClickListeners() // Добавляем обработчики кликов
        return binding.root
    }

    private fun setupProfileInfo() {
        binding.profileName.text = "Пользователь"
        binding.profileSubtitle.text = "Личный аккаунт"
        binding.booksCount.text = "4"
        binding.shelvesCount.text = "4"
    }

    private fun setupSwitches() {
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            // TODO: смена темы
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // TODO: уведомления
        }
    }

    private fun setupClickListeners() {
        // Обработка нажатия на всю область "Тёмная тема"
        binding.buttonDarkTheme.setOnClickListener {
            val switch = binding.switchDarkTheme
            switch.isChecked = !switch.isChecked
        }

        // Обработка нажатия на всю область "Уведомления"
        binding.buttonNotifications.setOnClickListener {
            val switch = binding.switchNotifications
            switch.isChecked = !switch.isChecked
        }

        // Обработка нажатия на всю область "Язык"
        binding.buttonLanguage.setOnClickListener {
            val currentLanguage = binding.textLanguageValue.text.toString()
            binding.textLanguageValue.text = if (currentLanguage == "Русский") {
                "English"
            } else {
                "Русский"
            }
        }

        // Обработка нажатия на кнопку редактирования профиля
        binding.editProfileButton.setOnClickListener {
            // TODO: редактирование профиля
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}