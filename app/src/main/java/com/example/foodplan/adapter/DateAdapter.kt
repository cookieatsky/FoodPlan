package com.example.foodplan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateAdapter(
    private val onDateSelected: (LocalDate) -> Unit
) : ListAdapter<LocalDate, DateAdapter.DateViewHolder>(DateDiffCallback()) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayOfWeekTextView: TextView = itemView.findViewById(R.id.dayOfWeekTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(date: LocalDate, isSelected: Boolean) {
            val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale("ru"))
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

            dayOfWeekTextView.text = date.format(dayOfWeekFormatter)
            dateTextView.text = date.format(dateFormatter)

            itemView.isSelected = isSelected
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onDateSelected(date)
            }
        }
    }

    private class DateDiffCallback : DiffUtil.ItemCallback<LocalDate>() {
        override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
            return oldItem == newItem
        }
    }
} 