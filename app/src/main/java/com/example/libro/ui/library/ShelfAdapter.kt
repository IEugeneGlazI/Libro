package com.example.libro.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libro.databinding.ItemShelfCardBinding

data class Shelf(
    val name: String,
    val location: String,
    val description: String,
    val booksCount: Int
)

class ShelfAdapter(private val shelves: List<Shelf>) :
    RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder>() {

    class ShelfViewHolder(val binding: ItemShelfCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfViewHolder {
        val binding = ItemShelfCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ShelfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShelfViewHolder, position: Int) {
        val shelf = shelves[position]
        with(holder.binding) {
            textShelfName.text = shelf.name
            textShelfLocation.text = "Расположение: ${shelf.location}"
            textShelfDescription.text = shelf.description
            textShelfBooksCount.text = "Книг: ${shelf.booksCount}"
        }
    }

    override fun getItemCount() = shelves.size
}