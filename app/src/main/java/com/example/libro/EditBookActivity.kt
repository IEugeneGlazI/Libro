package com.example.libro

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import coil.load
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityEditBookBinding
import com.google.android.material.chip.Chip

class EditBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBookBinding
    private var selectedImageUri: Uri? = null
    private var currentBook: Book? = null
    private var bookIndex: Int = -1

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.bookCoverPreview.load(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadBookData()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnSelectImage.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveBookChanges() }

        val genres = listOf("Классика", "Роман", "Фэнтези", "Детектив", "Научная фантастика", "Программирование", "Бизнес", "Психология", "История", "Биография", "Поэзия", "Драма")
        for (genre in genres) {
            val chip = Chip(this).apply { text = genre; isCheckable = true }
            binding.genreChipGroup.addView(chip)
        }
    }

    private fun loadBookData() {
        val book = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("BOOK_EXTRA", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Book>("BOOK_EXTRA")
        }
        bookIndex = intent.getIntExtra("BOOK_INDEX", -1)

        if (book != null && bookIndex != -1) {
            currentBook = book
            populateFields(book)
        } else {
            Toast.makeText(this, "Ошибка загрузки книги", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateFields(book: Book) {
        binding.inputTitle.setText(book.title)
        binding.inputAuthor.setText(book.author)
        binding.inputYear.setText(book.year.toString())
        binding.inputPublisher.setText(book.publisher)
        binding.inputDescription.setText(book.description)
        binding.inputIsbn.setText(book.isbn)
        binding.ratingBar.rating = book.rating.toFloat()
        binding.inputShelfNumber.setText(book.shelfNumber)
        binding.inputPlaceNumber.setText(book.placeNumber)

        // Load cover image
        if (!book.coverUrl.isNullOrEmpty()) {
            binding.bookCoverPreview.load(book.coverUrl) {
                placeholder(R.drawable.bg_book_cover_placeholder)
                error(R.drawable.bg_book_cover_placeholder)
            }
        }

        // Select chips
        for (chip in binding.genreChipGroup.children) {
            if (chip is Chip && book.tags.contains(chip.text)) {
                chip.isChecked = true
            }
        }
    }

    private fun saveBookChanges() {
        val title = binding.inputTitle.text.toString()
        if (title.isBlank()) {
            Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenres = binding.genreChipGroup.children
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }
            .toList()

        val coverUrl = selectedImageUri?.toString() ?: currentBook?.coverUrl
        val shelfNumber = binding.inputShelfNumber.text.toString().takeIf { it.isNotBlank() }
        val placeNumber = binding.inputPlaceNumber.text.toString().takeIf { it.isNotBlank() }

        val updatedBook = currentBook!!.copy(
            title = title,
            author = binding.inputAuthor.text.toString(),
            year = binding.inputYear.text.toString().toIntOrNull() ?: 0,
            publisher = binding.inputPublisher.text.toString(),
            description = binding.inputDescription.text.toString(),
            isbn = binding.inputIsbn.text.toString(),
            tags = selectedGenres,
            rating = binding.ratingBar.rating.toInt(),
            coverUrl = coverUrl,
            shelfNumber = shelfNumber,
            placeNumber = placeNumber
        )

        val books = JsonHelper.loadBooks(this)
        if (bookIndex >= 0 && bookIndex < books.size) {
            books[bookIndex] = updatedBook
            JsonHelper.saveBooks(this, books)
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Ошибка сохранения книги", Toast.LENGTH_SHORT).show()
        }
    }
}
