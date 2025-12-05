package com.example.libro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityCabinetBooksBinding

class CabinetBooksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCabinetBooksBinding
    private var bookList = mutableListOf<Book>()
    private lateinit var bookAdapter: BookAdapter

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCabinetBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        val shelfName = intent.getStringExtra("SHELF_NAME") ?: "Шкаф"
        binding.toolbar.title = shelfName
        binding.cabinetName.text = shelfName

        loadBooks()

        binding.fabAddBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            activityLauncher.launch(intent)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadBooks() {
        bookList = JsonHelper.loadBooks(this)
        if (bookList.isEmpty()) {
            bookList.addAll(getInitialBooks())
            JsonHelper.saveBooks(this, bookList)
        }
        bookAdapter.updateBooks(bookList)
        updateBookCount()
    }

    private fun getInitialBooks(): List<Book> {
        return listOf(
            Book(
                title = "Война и мир",
                author = "Лев Толстой",
                year = 1869,
                tags = listOf("Классика", "Роман"),
                status = "Прочитана",
                rating = 5,
                commentCount = 2,
                bookmarkCount = 5
            ),
            Book(
                title = "Преступление и наказание",
                author = "Фёдор Достоевский",
                year = 1866,
                tags = listOf("Классика"),
                status = "Читаю",
                rating = 4,
                commentCount = 2,
                bookmarkCount = 6
            )
        )
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(bookList) { book ->
            val bookIndex = bookList.indexOf(book)
            val intent = Intent(this, BookDetailsActivity::class.java).apply {
                putExtra("BOOK_EXTRA", book)
                putExtra("BOOK_INDEX", bookIndex)
            }
            activityLauncher.launch(intent)
        }
        binding.recyclerBooks.layoutManager = LinearLayoutManager(this)
        binding.recyclerBooks.adapter = bookAdapter
    }

    private fun updateBookCount() {
        binding.cabinetCount.text = "${bookList.size} книг"
    }
}