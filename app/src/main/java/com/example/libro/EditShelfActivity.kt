package com.example.libro

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityEditShelfBinding
import com.example.libro.databinding.DialogIconSelectorBinding
import com.example.libro.ui.library.IconSelectorAdapter
import com.example.libro.ui.library.Shelf
import com.google.android.material.bottomsheet.BottomSheetDialog

class EditShelfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditShelfBinding
    private var currentShelf: Shelf? = null
    private var shelfIndex: Int = -1
    private var selectedIconName: String = "ic_shelf_books"

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

        // Обработчик нажатия на preview иконки
        binding.shelfIconPreview.setOnClickListener {
            showIconSelectorDialog()
        }
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
            selectedIconName = shelf.iconName
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
        updateIconPreview(shelf.iconName)
    }

    private fun updateIconPreview(iconName: String) {
        val iconId = resources.getIdentifier(iconName, "drawable", packageName)
        if (iconId != 0) {
            binding.shelfIconPreview.setImageResource(iconId)
        } else {
            binding.shelfIconPreview.setImageResource(R.drawable.ic_shelf_books)
        }
    }

    private fun showIconSelectorDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogIconSelectorBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val icons = getAvailableIcons()
        val adapter = IconSelectorAdapter(icons, selectedIconName) { iconName ->
            selectedIconName = iconName
            updateIconPreview(iconName)
            dialog.dismiss()
        }

        dialogBinding.recyclerIcons.layoutManager = GridLayoutManager(this, 4)
        dialogBinding.recyclerIcons.adapter = adapter

        dialog.show()
    }

    private fun getAvailableIcons(): List<IconSelectorAdapter.IconItem> {
        return listOf(
            IconSelectorAdapter.IconItem("ic_shelf_books", R.drawable.ic_shelf_books),
            IconSelectorAdapter.IconItem("ic_moon", R.drawable.ic_moon),
            IconSelectorAdapter.IconItem("ic_brain", R.drawable.ic_brain),
            IconSelectorAdapter.IconItem("ic_briefcase", R.drawable.ic_briefcase),
            IconSelectorAdapter.IconItem("ic_art", R.drawable.ic_art),
            IconSelectorAdapter.IconItem("ic_clapper", R.drawable.ic_clapper),
            IconSelectorAdapter.IconItem("ic_game", R.drawable.ic_game),
            IconSelectorAdapter.IconItem("ic_guitar", R.drawable.ic_guitar),
            IconSelectorAdapter.IconItem("ic_footbal", R.drawable.ic_footbal),
            IconSelectorAdapter.IconItem("ic_cooking", R.drawable.ic_cooking),
            IconSelectorAdapter.IconItem("ic_sofa", R.drawable.ic_sofa),
            IconSelectorAdapter.IconItem("ic_plant", R.drawable.ic_plant),
            IconSelectorAdapter.IconItem("ic_house", R.drawable.ic_house),
            IconSelectorAdapter.IconItem("ic_book", R.drawable.ic_book),
            IconSelectorAdapter.IconItem("ic_stars", R.drawable.ic_stars),
            IconSelectorAdapter.IconItem("ic_books", R.drawable.ic_books)
        )
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
                description = binding.inputShelfNote.text.toString(),
                iconName = selectedIconName
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
