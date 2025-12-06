package com.example.libro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.R
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityCabinetBooksBinding

class CabinetBooksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCabinetBooksBinding
    private var bookList = mutableListOf<Book>()
    private lateinit var bookAdapter: BookAdapter
    private var emptyStateView: View? = null

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadBooks()
            updateShelfBooksCount()
        }
    }
    
    private fun updateShelfBooksCount() {
        val allBooks = JsonHelper.loadBooks(this)
        val booksInShelf = allBooks.count { it.shelfName == shelfName }
        
        // Обновляем счетчик в полке
        val shelves = JsonHelper.loadShelves(this)
        val shelfIndex = shelves.indexOfFirst { it.name == shelfName }
        if (shelfIndex != -1) {
            val shelf = shelves[shelfIndex]
            val updatedShelf = shelf.copy(booksCount = booksInShelf)
            shelves[shelfIndex] = updatedShelf
            JsonHelper.saveShelves(this, shelves)
        }
    }

    private var shelfName: String = "Шкаф"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCabinetBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        
        emptyStateView = binding.root.findViewById(R.id.empty_state)

        shelfName = intent.getStringExtra("SHELF_NAME") ?: "Шкаф"
        binding.toolbar.title = shelfName
        binding.cabinetName.text = shelfName
        
        updateShelfIcon()

        loadBooks()

        binding.fabAddBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java).apply {
                putExtra("SHELF_NAME", shelfName)
            }
            activityLauncher.launch(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadBooks()
        updateShelfBooksCount()
        updateShelfIcon()
    }
    
    private fun updateShelfIcon() {
        val shelves = JsonHelper.loadShelves(this)
        val shelf = shelves.find { it.name == shelfName }
        val iconName = shelf?.iconName ?: "ic_shelf_books"
        val iconId = resources.getIdentifier(iconName, "drawable", packageName)
        binding.cabinetIcon.setImageResource(if (iconId != 0) iconId else R.drawable.ic_shelf_books)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadBooks() {
        val allBooks = JsonHelper.loadBooks(this)
        // Фильтруем книги по названию полки
        bookList = allBooks.filter { it.shelfName == shelfName }.toMutableList()
        bookAdapter.updateBooks(bookList)
        updateBookCount()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        emptyStateView?.let { emptyView ->
            if (bookList.isEmpty()) {
                binding.recyclerBooks.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                binding.recyclerBooks.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
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
        binding.cabinetCount.text = formatBookCount(bookList.size)
    }
    
    private fun formatBookCount(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count книга"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count книги"
            else -> "$count книг"
        }
    }
}