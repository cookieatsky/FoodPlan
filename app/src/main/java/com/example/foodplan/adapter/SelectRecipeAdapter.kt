package com.example.foodplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.databinding.ItemSelectRecipeBinding
import com.example.foodplan.model.Recipe

class SelectRecipeAdapter(private val onRecipeSelected: (Recipe) -> Unit) :
    ListAdapter<Recipe, SelectRecipeAdapter.SelectRecipeViewHolder>(SelectRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectRecipeViewHolder {
        val binding = ItemSelectRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SelectRecipeViewHolder(private val binding: ItemSelectRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeSelected(getItem(position))
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.recipeNameTextView.text = recipe.name
            binding.cookingTimeTextView.text = "${recipe.cookingTime} мин"
            binding.caloriesTextView.text = "${recipe.calories} ккал"
            binding.servingsTextView.text = "${recipe.servings} порций"
        }
    }

    private class SelectRecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
} 