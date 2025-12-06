package com.example.libro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.libro.databinding.ItemBookCardBinding

class BookAdapter(
    private var books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(val binding: ItemBookCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        with(holder.binding) {
            bookTitle.text = book.title
            bookAuthor.text = book.author
            bookYear.text = book.year.toString()
            bookStatus.text = book.status
            // Отображаем номер полки и место вместо счетчиков комментариев и закладок
            commentCount.text = book.shelfNumber ?: "-"
            bookmarkCount.text = book.placeNumber ?: "-"

            // Load cover image
            if (!book.coverUrl.isNullOrEmpty()) {
                bookCover.load(book.coverUrl) {
                    placeholder(R.drawable.bg_book_cover_placeholder)
                    error(R.drawable.bg_book_cover_placeholder)
                }
            } else {
                bookCover.load(R.drawable.bg_book_cover_placeholder)
            }

            // Handle rating stars
            ratingBarLayout.removeAllViews()
            for (i in 0 until book.rating) {
                val star = ImageView(root.context).apply {
                    setImageResource(R.drawable.ic_star)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                ratingBarLayout.addView(star)
            }

            // Handle tags
            val tags = listOf(tag1, tag2)
            for (i in tags.indices) {
                if (i < book.tags.size) {
                    tags[i].text = book.tags[i]
                    tags[i].visibility = View.VISIBLE
                } else {
                    tags[i].visibility = View.GONE
                }
            }
        }
        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        this.books = newBooks
        notifyDataSetChanged()
    }
}