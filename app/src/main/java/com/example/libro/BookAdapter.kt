package com.example.libro

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
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
            bookPublisher.text = book.publisher ?: ""
            bookPublisher.visibility = if (book.publisher.isNullOrBlank()) View.GONE else View.VISIBLE
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
            tag1.visibility = View.GONE
            tag2.visibility = View.GONE
            tagExtra.visibility = View.GONE
            
            when {
                book.tags.isEmpty() -> {
                    // Нет жанров
                }
                book.tags.size == 1 -> {
                    tag1.text = book.tags[0]
                    tag1.visibility = View.VISIBLE
                }
                book.tags.size == 2 -> {
                    tag1.text = book.tags[0]
                    tag1.visibility = View.VISIBLE
                    tag2.text = book.tags[1]
                    tag2.visibility = View.VISIBLE
                }
                else -> {
                    // Показываем первые 2 жанра и "+N"
                    tag1.text = book.tags[0]
                    tag1.visibility = View.VISIBLE
                    tag2.text = book.tags[1]
                    tag2.visibility = View.VISIBLE
                    tagExtra.text = "+${book.tags.size - 2}"
                    tagExtra.visibility = View.VISIBLE
                }
            }
            
            // Устанавливаем цвет фона статуса
            val statusColorRes = when (book.status.lowercase()) {
                "читаю" -> R.color.status_reading
                "прочитана" -> R.color.status_read
                "не начата" -> R.color.status_not_started
                "отложена" -> R.color.status_postponed
                else -> R.color.status_not_started
            }
            val statusColor = ContextCompat.getColor(root.context, statusColorRes)
            val drawable = GradientDrawable().apply {
                cornerRadius = 16f * root.context.resources.displayMetrics.density
                setColor(statusColor)
            }
            bookStatus.background = drawable
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