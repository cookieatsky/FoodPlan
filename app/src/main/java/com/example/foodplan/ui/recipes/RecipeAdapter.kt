package com.example.foodplan.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import com.example.foodplan.model.Recipe

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescriptionTextView)
        private val cookingTimeTextView: TextView = itemView.findViewById(R.id.cookingTimeTextView)
        private val servingsTextView: TextView = itemView.findViewById(R.id.servingsTextView)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeClick(getItem(position))
                }
            }
        }

        fun bind(recipe: Recipe) {
            nameTextView.text = recipe.name
            descriptionTextView.text = recipe.description
            cookingTimeTextView.text = "Время приготовления: ${recipe.cookingTime} мин"
            servingsTextView.text = "Порций: ${recipe.servings}"

            // TODO: Загрузка изображения рецепта
            recipe.imageUri?.let { _ ->
                // Здесь будет код для загрузки изображения
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