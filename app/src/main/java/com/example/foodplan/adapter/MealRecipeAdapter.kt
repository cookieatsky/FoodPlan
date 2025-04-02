package com.example.foodplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.databinding.ItemMealRecipeBinding
import com.example.foodplan.model.Recipe

class MealRecipeAdapter(private val onRecipeClick: (Recipe) -> Unit) :
    ListAdapter<Recipe, MealRecipeAdapter.MealRecipeViewHolder>(MealRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealRecipeViewHolder {
        val binding = ItemMealRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MealRecipeViewHolder(private val binding: ItemMealRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeClick(getItem(position))
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.recipeNameTextView.text = recipe.name
            binding.cookingTimeTextView.text = "${recipe.cookingTime} мин"
            binding.caloriesTextView.text = "${recipe.calories} ккал"
        }
    }

    private class MealRecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
} 