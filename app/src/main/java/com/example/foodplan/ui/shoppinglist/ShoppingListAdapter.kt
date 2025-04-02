package com.example.foodplan.ui.shoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.databinding.ItemShoppingListBinding
import com.example.foodplan.model.ShoppingListItem
import java.text.SimpleDateFormat
import java.util.*

class ShoppingListAdapter(
    private val onItemChecked: (ShoppingListItem) -> Unit,
    private val onItemDelete: (ShoppingListItem) -> Unit
) : ListAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListViewHolder>(ShoppingListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShoppingListViewHolder(
        private val binding: ItemShoppingListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        fun bind(item: ShoppingListItem) {
            binding.apply {
                nameTextView.text = item.name
                dateTextView.text = dateFormat.format(Date(item.date))
                checkBox.isChecked = item.isChecked
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onItemChecked(item.copy(isChecked = isChecked))
                }
                deleteButton.setOnClickListener {
                    onItemDelete(item)
                }
            }
        }
    }

    private class ShoppingListDiffCallback : DiffUtil.ItemCallback<ShoppingListItem>() {
        override fun areItemsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem == newItem
        }
    }
} 