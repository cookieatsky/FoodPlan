package com.example.foodplan.ui.recipes

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R

class EditableTextAdapter(
    private val onDeleteClick: (Int) -> Unit
) : ListAdapter<String, EditableTextAdapter.EditableTextViewHolder>(EditableTextDiffCallback()) {

    private var items: List<String> = emptyList()

    override fun submitList(list: List<String>?) {
        items = list ?: emptyList()
        super.submitList(list)
    }

    fun getItems(): List<String> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditableTextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editable_text, parent, false)
        return EditableTextViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditableTextViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class EditableTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val editText: EditText = itemView.findViewById(R.id.editText)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        init {
            deleteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
                }
            }
        }

        fun bind(text: String, position: Int) {
            editText.setText(text)
            
            // Удаляем предыдущий TextWatcher, если он есть
            editText.tag?.let { tag ->
                if (tag is TextWatcher) {
                    editText.removeTextChangedListener(tag)
                }
            }

            // Создаем новый TextWatcher
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val currentList = items.toMutableList()
                    currentList[position] = s.toString()
                    items = currentList
                }
            }

            // Сохраняем ссылку на TextWatcher и добавляем его
            editText.tag = textWatcher
            editText.addTextChangedListener(textWatcher)
        }
    }

    private class EditableTextDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
} 