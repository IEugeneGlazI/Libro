package com.example.libro.ui.profile

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.example.libro.R
import com.example.libro.data.ImageHelper
import com.example.libro.data.JsonHelper
import com.example.libro.data.ProfileHelper
import com.example.libro.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            saveProfileImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupProfileInfo()
        setupSwitches()
        setupClickListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadProfileInfo()
    }

    private fun setupProfileInfo() {
        loadProfileInfo()
        
        val books = JsonHelper.loadBooks(requireContext())
        val shelves = JsonHelper.loadShelves(requireContext())
        binding.booksCount.text = books.size.toString()
        binding.shelvesCount.text = shelves.size.toString()
    }

    private fun loadProfileInfo() {
        val profile = ProfileHelper.loadProfile(requireContext())
        binding.profileName.text = profile.userName
        binding.profileSubtitle.text = "Личный аккаунт"
        
        // Загружаем изображение профиля
        if (!profile.profileImagePath.isNullOrEmpty()) {
            binding.profileImage.load(profile.profileImagePath) {
                placeholder(R.drawable.ic_my_profile)
                error(R.drawable.ic_my_profile)
            }
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_my_profile)
        }
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
            showEditNameDialog()
        }

        // Обработка нажатия на изображение профиля
        binding.profileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun showEditNameDialog() {
        val profile = ProfileHelper.loadProfile(requireContext())
        val input = EditText(requireContext()).apply {
            setText(profile.userName)
            hint = "Введите имя"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить имя")
            .setView(input)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedProfile = profile.copy(userName = newName)
                    ProfileHelper.saveProfile(requireContext(), updatedProfile)
                    binding.profileName.text = newName
                    Toast.makeText(requireContext(), "Имя изменено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveProfileImage(uri: Uri) {
        try {
            // Сохраняем изображение в постоянное хранилище
            val savedPath = ImageHelper.saveImageToInternalStorage(
                requireContext(),
                uri,
                "profile"
            )

            if (savedPath != null) {
                val profile = ProfileHelper.loadProfile(requireContext())
                val updatedProfile = profile.copy(profileImagePath = savedPath)
                ProfileHelper.saveProfile(requireContext(), updatedProfile)
                
                // Обновляем изображение
                binding.profileImage.load(savedPath) {
                    placeholder(R.drawable.ic_my_profile)
                    error(R.drawable.ic_my_profile)
                }
                Toast.makeText(requireContext(), "Изображение профиля изменено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Ошибка сохранения изображения", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка сохранения изображения", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}