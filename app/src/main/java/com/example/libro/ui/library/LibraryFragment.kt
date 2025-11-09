package com.example.libro.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.R
import com.example.libro.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShelfAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ShelfAdapter(
            listOf(
                Shelf("Шкаф у окна", "Гостиная", "Художественная литература", 25),
                Shelf("Рабочий стол", "Кабинет", "Научные книги и документы", 12),
                Shelf("Шкаф у окна", "Гостиная", "Художественная литература", 25),
                Shelf("Рабочий стол", "Кабинет", "Научные книги и документы", 12),
                Shelf("Шкаф у окна", "Гостиная", "Художественная литература", 25),
                Shelf("Рабочий стол", "Кабинет", "Научные книги и документы", 12),
                Shelf("Шкаф у окна", "Гостиная", "Художественная литература", 25),
                Shelf("Рабочий стол", "Кабинет", "Научные книги и документы", 12),
                Shelf("Шкаф у окна", "Гостиная", "Художественная литература", 25),
                Shelf("Рабочий стол", "Кабинет", "Научные книги и документы", 12)
            )
        )
        binding.recyclerViewShelves.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewShelves.adapter = adapter

        binding.fabAddShelf.setOnClickListener {
            // TODO: Открыть диалог добавления нового шкафа
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
