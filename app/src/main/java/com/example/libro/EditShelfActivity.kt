package com.example.libro

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityEditShelfBinding
import com.example.libro.ui.library.Shelf

class EditShelfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditShelfBinding
    private var currentShelf: Shelf? = null
    private var shelfIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditShelfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadShelfData()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnSaveShelf.setOnClickListener { saveShelfChanges() }
    }

    private fun loadShelfData() {
        val shelf = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SHELF_EXTRA", Shelf::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Shelf>("SHELF_EXTRA")
        }
        shelfIndex = intent.getIntExtra("SHELF_INDEX", -1)

        if (shelf != null && shelfIndex != -1) {
            currentShelf = shelf
            populateFields(shelf)
        } else {
            Toast.makeText(this, "Ошибка загрузки полки", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateFields(shelf: Shelf) {
        binding.inputShelfName.setText(shelf.name)
        binding.inputShelfRoom.setText(shelf.location)
        binding.inputShelfNote.setText(shelf.description)
    }

    private fun saveShelfChanges() {
        val shelfName = binding.inputShelfName.text.toString()
        if (shelfName.isBlank()) {
            Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        currentShelf?.let {
            val updatedShelf = it.copy(
                name = shelfName,
                location = binding.inputShelfRoom.text.toString(),
                description = binding.inputShelfNote.text.toString()
            )

            val shelves = JsonHelper.loadShelves(this)
            if (shelfIndex >= 0 && shelfIndex < shelves.size) {
                shelves[shelfIndex] = updatedShelf
                JsonHelper.saveShelves(this, shelves)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Ошибка сохранения полки", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
