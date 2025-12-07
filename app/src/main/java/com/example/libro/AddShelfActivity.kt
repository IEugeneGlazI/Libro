package com.example.libro

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityAddShelfBinding
import com.example.libro.databinding.DialogIconSelectorBinding
import com.example.libro.ui.library.IconSelectorAdapter
import com.example.libro.ui.library.Shelf
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddShelfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddShelfBinding
    private var selectedIconName: String = "ic_book"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddShelfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Устанавливаем начальную иконку
        updateIconPreview(selectedIconName)

        // Обработчик нажатия на preview иконки
        binding.shelfIconPreview.setOnClickListener {
            showIconSelectorDialog()
        }

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
                booksCount = 0,
                iconName = selectedIconName
            )

            val shelves = JsonHelper.loadShelves(this)
            shelves.add(newShelf)
            JsonHelper.saveShelves(this, shelves)

            setResult(Activity.RESULT_OK)
            finish()
        }
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
            IconSelectorAdapter.IconItem("ic_sword", R.drawable.ic_sword),
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
}
