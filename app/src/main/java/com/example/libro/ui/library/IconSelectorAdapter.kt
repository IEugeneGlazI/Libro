package com.example.libro.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libro.databinding.ItemIconSelectorBinding

class IconSelectorAdapter(
    private val icons: List<IconItem>,
    private val selectedIconName: String?,
    private val onIconSelected: (String) -> Unit
) : RecyclerView.Adapter<IconSelectorAdapter.IconViewHolder>() {

    data class IconItem(
        val iconName: String,
        val iconResId: Int
    )

    class IconViewHolder(val binding: ItemIconSelectorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconSelectorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconItem = icons[position]
        val isSelected = iconItem.iconName == selectedIconName

        with(holder.binding) {
            iconImage.setImageResource(iconItem.iconResId)
            iconSelectedIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE

            root.setOnClickListener {
                onIconSelected(iconItem.iconName)
            }
        }
    }

    override fun getItemCount() = icons.size
}

