package com.example.libro

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityBookDetailsBinding
import com.google.android.material.chip.Chip

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private var currentBook: Book? = null
    private var bookIndex: Int = -1
    private var dataHasChanged = false

    private val editBookLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dataHasChanged = true
            loadBooksAndRefresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadBookData()
        setupEditButton()
        setupStatusListener()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_book -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удалить книгу")
            .setMessage("Вы уверены, что хотите удалить эту книгу?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteBook()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteBook() {
        val books = JsonHelper.loadBooks(this)
        if (bookIndex >= 0 && bookIndex < books.size) {
            val bookToDelete = books[bookIndex]
            // Удаляем изображение книги, если оно локальное
            bookToDelete.coverUrl?.let { coverUrl ->
                if (com.example.libro.data.ImageHelper.isLocalFile(coverUrl)) {
                    com.example.libro.data.ImageHelper.deleteImageFile(this, coverUrl)
                }
            }
            books.removeAt(bookIndex)
            JsonHelper.saveBooks(this, books)
            dataHasChanged = true
            finish()
        } else {
            Toast.makeText(this, "Ошибка удаления книги", Toast.LENGTH_SHORT).show()
        }
    }

    override fun finish() {
        if (dataHasChanged) {
            setResult(Activity.RESULT_OK)
        }
        super.finish()
    }

    private fun loadBookData() {
        val book = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("BOOK_EXTRA", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Book>("BOOK_EXTRA")
        }
        bookIndex = intent.getIntExtra("BOOK_INDEX", -1)

        if (book != null) {
            currentBook = book
            populateViews(book)
        } else {
            finish()
        }
    }

    private fun populateViews(book: Book) {
        // Load cover image
        if (!book.coverUrl.isNullOrEmpty()) {
            binding.bookCover.load(book.coverUrl) {
                placeholder(R.drawable.bg_book_cover_placeholder)
                error(R.drawable.bg_book_cover_placeholder)
            }
        } else {
            binding.bookCover.load(R.drawable.bg_book_cover_placeholder)
        }

        binding.bookTitle.text = book.title
        binding.bookAuthor.text = book.author
        binding.bookPublisher.text = "Издательство: ${book.publisher}"
        binding.bookDescription.text = book.description
        // Используем расположение (позицию) шкафа из книги
        binding.shelfName.text = book.shelfLocation ?: "Не указано"
        // Формируем строку позиции из номера полки и места
        val positionText = when {
            !book.shelfNumber.isNullOrBlank() && !book.placeNumber.isNullOrBlank() -> 
                "Полка: ${book.shelfNumber}  Место: ${book.placeNumber}"
            !book.shelfNumber.isNullOrBlank() -> 
                "Полка: ${book.shelfNumber}"
            !book.placeNumber.isNullOrBlank() -> 
                "Место: ${book.placeNumber}"
            else -> book.shelfPosition ?: "Не указано"
        }
        binding.shelfPosition.text = positionText
        binding.isbnText.text = book.isbn

        binding.ratingBar.removeAllViews()
        for (i in 0 until book.rating) {
            val star = ImageView(this).apply {
                setImageResource(R.drawable.ic_star)
                layoutParams = ViewGroup.LayoutParams(48, 48)
            }
            binding.ratingBar.addView(star)
        }

        // Устанавливаем выбранный статус
        binding.statusNotStarted.isChecked = book.status.equals("Не начата", ignoreCase = true)
        binding.statusReading.isChecked = book.status.equals("Читаю", ignoreCase = true)
        binding.statusRead.isChecked = book.status.equals("Прочитана", ignoreCase = true)
        binding.statusPostponed.isChecked = book.status.equals("Отложена", ignoreCase = true)
        
        // Re-set listener
        setupStatusListener()

        binding.genreChipGroup.removeAllViews()
        for (tag in book.tags) {
            val chip = Chip(this, null, R.style.Widget_App_Chip).apply {
                text = tag
            }
            binding.genreChipGroup.addView(chip)
        }
    }

    private fun setupEditButton() {
        binding.btnEdit.setOnClickListener {
            currentBook?.let {
                val intent = Intent(this, EditBookActivity::class.java).apply {
                    putExtra("BOOK_EXTRA", it)
                    putExtra("BOOK_INDEX", bookIndex)
                }
                editBookLauncher.launch(intent)
            }
        }
    }

    private fun setupStatusListener() {
        val statusChips = listOf(
            binding.statusNotStarted,
            binding.statusReading,
            binding.statusRead,
            binding.statusPostponed
        )
        
        statusChips.forEach { chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Снимаем выделение с других чипов
                    statusChips.forEach { otherChip ->
                        if (otherChip != chip) {
                            otherChip.isChecked = false
                        }
                    }
                    
                    val newStatus = chip.text.toString()
                    currentBook?.let { book ->
                        if (book.status != newStatus) {
                            val updatedBook = book.copy(status = newStatus)
                            currentBook = updatedBook
                            dataHasChanged = true

                            val books = JsonHelper.loadBooks(this)
                            if (bookIndex >= 0 && bookIndex < books.size) {
                                books[bookIndex] = updatedBook
                                JsonHelper.saveBooks(this, books)
                                Toast.makeText(this, "Статус изменен на \"$newStatus\"", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadBooksAndRefresh() {
        val books = JsonHelper.loadBooks(this)
        if (bookIndex != -1 && bookIndex < books.size) {
            currentBook = books[bookIndex]
            currentBook?.let { populateViews(it) }
        } else {
            finish()
        }
    }
}
