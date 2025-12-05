package com.example.libro.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.libro.R
import com.example.libro.databinding.ItemShelfCardBinding

class ShelfAdapter(
    private var shelves: MutableList<Shelf>,
    private val onShelfClick: (Shelf) -> Unit,
    private val onEditClick: (Shelf, Int) -> Unit,
    private val onDeleteClick: (Shelf, Int) -> Unit
) : RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder>() {

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
            textShelfLocation.text = shelf.location
            textShelfDescription.text = shelf.description
            textShelfBooksCount.text = "${shelf.booksCount} книг"

            val context = holder.itemView.context
            val iconId = context.resources.getIdentifier(shelf.iconName, "drawable", context.packageName)
            iconShelf.setImageResource(if (iconId != 0) iconId else R.drawable.ic_shelf_books)

            holder.itemView.setOnClickListener {
                onShelfClick(shelf)
            }

            btnShelfOptions.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.shelf_options_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit_shelf -> {
                            onEditClick(shelf, position)
                            true
                        }
                        R.id.action_delete_shelf -> {
                            onDeleteClick(shelf, position)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun getItemCount() = shelves.size

    fun updateShelves(newShelves: MutableList<Shelf>) {
        this.shelves = newShelves
        notifyDataSetChanged()
    }
}