package com.example.libro.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.Book
import com.example.libro.BookAdapter
import com.example.libro.BookDetailsActivity
import com.example.libro.R
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private var allBooks: List<Book> = emptyList()
    private var filteredBooks: List<Book> = emptyList()

    // Фильтры
    private val selectedGenres = mutableSetOf<String>()
    private val selectedLocations = mutableSetOf<String>()
    private val selectedStatuses = mutableSetOf<String>()
    private var yearFrom: Int? = null
    private var yearTo: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFilters()
        setupSearchInput()
        loadBooks()
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(emptyList()) { book ->
            val books = JsonHelper.loadBooks(requireContext())
            val bookIndex = books.indexOfFirst { 
                it.title == book.title && it.author == book.author 
            }
            val intent = Intent(requireContext(), BookDetailsActivity::class.java).apply {
                putExtra("BOOK_EXTRA", book)
                putExtra("BOOK_INDEX", if (bookIndex >= 0) bookIndex else 0)
            }
            startActivity(intent)
        }
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResults.adapter = bookAdapter
    }

    private fun setupSearchInput() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch()
            }
        })
    }

    private fun setupFilters() {
        // Кнопка фильтров
        binding.btnFilters.setOnClickListener {
            val isVisible = binding.filtersPanel.visibility == View.VISIBLE
            binding.filtersPanel.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        // Загружаем книги и шкафы для получения уникальных значений
        val books = JsonHelper.loadBooks(requireContext())
        val shelves = JsonHelper.loadShelves(requireContext())

        // Настройка жанров
        val allGenres = books.flatMap { it.tags }.distinct().sorted()
        allGenres.forEach { genre ->
            val chip = Chip(requireContext()).apply {
                text = genre
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedGenres.add(genre)
                    } else {
                        selectedGenres.remove(genre)
                    }
                    updateResetButtonVisibility()
                    performSearch()
                }
            }
            binding.genreChipGroup.addView(chip)
        }

        // Настройка расположений
        val allLocations = shelves.map { it.location }.distinct().sorted()
        allLocations.forEach { location ->
            val chip = Chip(requireContext()).apply {
                text = location
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedLocations.add(location)
                    } else {
                        selectedLocations.remove(location)
                    }
                    updateResetButtonVisibility()
                    performSearch()
                }
            }
            binding.locationChipGroup.addView(chip)
        }

        // Настройка статусов
        val statuses = listOf("Не начата", "Читаю", "Прочитана", "Отложена")
        statuses.forEach { status ->
            val chip = Chip(requireContext()).apply {
                text = status
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedStatuses.add(status)
                    } else {
                        selectedStatuses.remove(status)
                    }
                    updateResetButtonVisibility()
                    performSearch()
                }
            }
            binding.statusChipGroup.addView(chip)
        }

        // Настройка года издания
        binding.yearFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yearFrom = s?.toString()?.toIntOrNull()
                updateResetButtonVisibility()
                performSearch()
            }
        })

        binding.yearTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yearTo = s?.toString()?.toIntOrNull()
                updateResetButtonVisibility()
                performSearch()
            }
        })

        // Кнопка сброса фильтров
        binding.btnResetFilters.setOnClickListener {
            resetAllFilters()
        }

        // Инициализируем видимость кнопки сброса
        updateResetButtonVisibility()
    }

    private fun loadBooks() {
        allBooks = JsonHelper.loadBooks(requireContext())
        performSearch()
    }

    private fun performSearch() {
        val searchQuery = binding.searchInput.text.toString().lowercase().trim()
        
        filteredBooks = allBooks.filter { book ->
            // Поиск по названию или автору
            val matchesSearch = searchQuery.isEmpty() || 
                book.title.lowercase().contains(searchQuery) ||
                book.author.lowercase().contains(searchQuery)

            // Фильтр по жанрам
            val matchesGenres = selectedGenres.isEmpty() || 
                book.tags.any { it in selectedGenres }

            // Фильтр по расположению
            val matchesLocation = selectedLocations.isEmpty() || 
                book.shelfLocation in selectedLocations

            // Фильтр по статусу
            val matchesStatus = selectedStatuses.isEmpty() || 
                book.status in selectedStatuses

            // Фильтр по году издания
            val matchesYear = (yearFrom == null || book.year >= yearFrom!!) &&
                (yearTo == null || book.year <= yearTo!!)

            matchesSearch && matchesGenres && matchesLocation && matchesStatus && matchesYear
        }

        bookAdapter.updateBooks(filteredBooks)
        updateResultsCount()
    }

    private fun updateResultsCount() {
        val count = filteredBooks.size
        val countText = when {
            count % 10 == 1 && count % 100 != 11 -> "Найдено: $count книга"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "Найдено: $count книги"
            else -> "Найдено: $count книг"
        }
        binding.searchResultsCount.text = countText
    }

    private fun updateResetButtonVisibility() {
        val hasActiveFilters = selectedGenres.isNotEmpty() ||
                selectedLocations.isNotEmpty() ||
                selectedStatuses.isNotEmpty() ||
                yearFrom != null ||
                yearTo != null
        
        binding.btnResetFilters.visibility = if (hasActiveFilters) View.VISIBLE else View.GONE
    }

    private fun resetAllFilters() {
        // Сбрасываем жанры
        selectedGenres.clear()
        for (i in 0 until binding.genreChipGroup.childCount) {
            val chip = binding.genreChipGroup.getChildAt(i) as Chip
            chip.isChecked = false
        }

        // Сбрасываем расположения
        selectedLocations.clear()
        for (i in 0 until binding.locationChipGroup.childCount) {
            val chip = binding.locationChipGroup.getChildAt(i) as Chip
            chip.isChecked = false
        }

        // Сбрасываем статусы
        selectedStatuses.clear()
        for (i in 0 until binding.statusChipGroup.childCount) {
            val chip = binding.statusChipGroup.getChildAt(i) as Chip
            chip.isChecked = false
        }

        // Сбрасываем год издания
        yearFrom = null
        yearTo = null
        binding.yearFrom.setText("")
        binding.yearTo.setText("")

        updateResetButtonVisibility()
        performSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
