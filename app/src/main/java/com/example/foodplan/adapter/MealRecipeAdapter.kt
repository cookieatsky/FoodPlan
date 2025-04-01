package com.example.foodplan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.model.Recipe

class MealRecipeAdapter(
    private val onDeleteClick: (Recipe) -> Unit
) : ListAdapter<Recipe, MealRecipeAdapter.MealRecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealRecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_recipe, parent, false)
        return MealRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MealRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeDescriptionTextView: TextView = itemView.findViewById(R.id.recipeDescriptionTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.name
            recipeDescriptionTextView.text = recipe.description

            // TODO: Загрузка изображения рецепта

            deleteButton.setOnClickListener {
                onDeleteClick(recipe)
            }
        }
    }

    private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
} 