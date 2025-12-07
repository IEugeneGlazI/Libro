package com.example.libro.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.libro.R
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.FragmentStatsBinding
import com.example.libro.databinding.ItemGenreStatBinding
import com.example.libro.databinding.ItemShelfStatBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStatistics()
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }

    private fun loadStatistics() {
        val books = JsonHelper.loadBooks(requireContext())
        val shelves = JsonHelper.loadShelves(requireContext())

        // Подсчет статистики по статусам
        val totalBooks = books.size
        val readBooks = books.count { it.status.equals("Прочитана", ignoreCase = true) }
        val readingBooks = books.count { it.status.equals("Читаю", ignoreCase = true) }
        val postponedBooks = books.count { it.status.equals("Отложена", ignoreCase = true) }
        val plannedBooks = books.count { it.status.equals("Не начата", ignoreCase = true) }
        val totalShelves = shelves.size

        // Обновляем карточки статистики
        binding.totalBooksCount.text = totalBooks.toString()
        binding.readBooksCount.text = readBooks.toString()
        binding.readingBooksCount.text = readingBooks.toString()
        binding.postponedBooksCount.text = postponedBooks.toString()
        binding.plannedBooksCount.text = plannedBooks.toString()
        binding.shelvesCount.text = totalShelves.toString()

        // Обновляем средний рейтинг
        updateAverageRating(books)
        
        // Обновляем книги на шкаф
        updateBooksPerShelf(books, shelves)
        
        // Обновляем топ жанров
        updateTopGenres(books)
        
        // Обновляем распределение по шкафам
        updateShelvesDistribution(books, shelves)

        // Обновляем прогресс чтения
        updateReadingProgress(readBooks, readingBooks, postponedBooks, plannedBooks, totalBooks)
    }
    
    private fun updateAverageRating(books: List<com.example.libro.Book>) {
        val ratedBooks = books.filter { it.rating > 0 }
        if (ratedBooks.isEmpty()) {
            binding.averageRating.text = "0.0"
            binding.ratedBooksCount.text = "Оценено книг: 0"
        } else {
            val average = ratedBooks.map { it.rating }.average()
            binding.averageRating.text = String.format("%.1f", average)
            binding.ratedBooksCount.text = "Оценено книг: ${ratedBooks.size}"
        }
    }
    
    private fun updateBooksPerShelf(books: List<com.example.libro.Book>, shelves: List<com.example.libro.ui.library.Shelf>) {
        if (shelves.isEmpty()) {
            binding.booksPerShelf.text = "0.0"
        } else {
            val average = books.size.toFloat() / shelves.size
            binding.booksPerShelf.text = String.format("%.1f", average)
        }
    }
    
    private fun updateTopGenres(books: List<com.example.libro.Book>) {
        // Подсчитываем количество книг по жанрам
        val genreCounts = mutableMapOf<String, Int>()
        books.forEach { book ->
            book.tags.forEach { tag ->
                genreCounts[tag] = genreCounts.getOrDefault(tag, 0) + 1
            }
        }
        
        // Сортируем по убыванию и берем топ-5
        val topGenres = genreCounts.toList()
            .sortedByDescending { it.second }
            .take(5)
        
        binding.genresList.removeAllViews()
        
        if (topGenres.isEmpty()) {
            val emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.item_genre_stat, binding.genresList, false)
            val emptyBinding = ItemGenreStatBinding.bind(emptyView)
            emptyBinding.genreName.text = "Нет данных"
            emptyBinding.genreCount.text = "0"
            emptyBinding.genreProgress.progress = 0
            binding.genresList.addView(emptyView)
        } else {
            val maxCount = topGenres.maxOfOrNull { it.second } ?: 1
            
            topGenres.forEach { (genre, count) ->
                val genreView = LayoutInflater.from(requireContext()).inflate(R.layout.item_genre_stat, binding.genresList, false)
                val genreBinding = ItemGenreStatBinding.bind(genreView)
                
                genreBinding.genreName.text = genre
                genreBinding.genreCount.text = count.toString()
                genreBinding.genreProgress.max = 100
                genreBinding.genreProgress.progress = (count * 100 / maxCount)
                
                binding.genresList.addView(genreView)
            }
        }
    }
    
    private fun updateShelvesDistribution(books: List<com.example.libro.Book>, shelves: List<com.example.libro.ui.library.Shelf>) {
        // Подсчитываем количество книг по шкафам
        val shelfCounts = mutableMapOf<String, Int>()
        books.forEach { book ->
            book.shelfName?.let { shelfName ->
                shelfCounts[shelfName] = shelfCounts.getOrDefault(shelfName, 0) + 1
            }
        }
        
        // Добавляем шкафы без книг
        shelves.forEach { shelf ->
            if (!shelfCounts.containsKey(shelf.name)) {
                shelfCounts[shelf.name] = 0
            }
        }
        
        // Сортируем по убыванию
        val sortedShelves = shelfCounts.toList()
            .sortedByDescending { it.second }
        
        binding.shelvesList.removeAllViews()
        
        if (sortedShelves.isEmpty()) {
            val emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.item_shelf_stat, binding.shelvesList, false)
            val emptyBinding = ItemShelfStatBinding.bind(emptyView)
            emptyBinding.shelfName.text = "Нет данных"
            emptyBinding.shelfCount.text = "0"
            emptyBinding.shelfProgress.progress = 0
            binding.shelvesList.addView(emptyView)
        } else {
            val maxCount = sortedShelves.maxOfOrNull { it.second } ?: 1
            
            sortedShelves.forEach { (shelfName, count) ->
                val shelf = shelves.find { it.name == shelfName }
                val shelfView = LayoutInflater.from(requireContext()).inflate(R.layout.item_shelf_stat, binding.shelvesList, false)
                val shelfBinding = ItemShelfStatBinding.bind(shelfView)
                
                shelfBinding.shelfName.text = shelfName
                shelfBinding.shelfCount.text = count.toString()
                shelfBinding.shelfProgress.max = 100
                shelfBinding.shelfProgress.progress = if (maxCount > 0) (count * 100 / maxCount) else 0
                
                // Устанавливаем иконку шкафа
                shelf?.iconName?.let { iconName ->
                    val iconResId = resources.getIdentifier(iconName, "drawable", requireContext().packageName)
                    if (iconResId != 0) {
                        shelfBinding.shelfIcon.setImageResource(iconResId)
                    }
                }
                
                binding.shelvesList.addView(shelfView)
            }
        }
    }

    private fun updateReadingProgress(read: Int, reading: Int, postponed: Int, planned: Int, total: Int) {
        if (total == 0) {
            binding.progressText.text = "0% завершено"
            binding.progressCircle.readPercent = 0f
            binding.progressCircle.readingPercent = 0f
            binding.progressCircle.postponedPercent = 0f
            binding.progressCircle.plannedPercent = 0f
            binding.legendRead.text = "Прочитано: 0"
            binding.legendReading.text = "Читаю: 0"
            binding.legendPostponed.text = "Отложено: 0"
            binding.legendPlanned.text = "В планах: 0"
            return
        }

        // Вычисляем проценты для каждого статуса
        val readPercent = (read * 100f) / total
        val readingPercent = (reading * 100f) / total
        val postponedPercent = (postponed * 100f) / total
        val plannedPercent = (planned * 100f) / total

        // Вычисляем процент завершенных (прочитанных)
        val completedPercent = readPercent.toInt()
        binding.progressText.text = "$completedPercent% завершено"
        
        // Обновляем круговой график
        binding.progressCircle.readPercent = readPercent
        binding.progressCircle.readingPercent = readingPercent
        binding.progressCircle.postponedPercent = postponedPercent
        binding.progressCircle.plannedPercent = plannedPercent

        // Обновляем легенду
        binding.legendRead.text = "Прочитано: $read"
        binding.legendReading.text = "Читаю: $reading"
        binding.legendPostponed.text = "Отложено: $postponed"
        binding.legendPlanned.text = "В планах: $planned"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
