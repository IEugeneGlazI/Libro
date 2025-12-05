package com.example.libro

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityAddShelfBinding
import com.example.libro.ui.library.Shelf

class AddShelfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddShelfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddShelfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCreateShelf.setOnClickListener {
            val shelfName = binding.inputShelfName.text.toString()
            if (shelfName.isBlank()) {
                Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newShelf = Shelf(
                name = shelfName,
                location = binding.inputShelfRoom.text.toString(),
                description = binding.inputShelfNote.text.toString(),
                booksCount = 0
            )

            val shelves = JsonHelper.loadShelves(this)
            shelves.add(newShelf)
            JsonHelper.saveShelves(this, shelves)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
