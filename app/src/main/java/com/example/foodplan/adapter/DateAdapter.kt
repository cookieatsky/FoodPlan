package com.example.foodplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplan.databinding.ItemDateBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateAdapter(private val onDateSelected: (LocalDate) -> Unit) :
    ListAdapter<LocalDate, DateAdapter.DateViewHolder>(DateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDateSelected(getItem(position))
                }
            }
        }

        fun bind(date: LocalDate) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM")
            binding.dateTextView.text = date.format(formatter)
            binding.dayOfWeekTextView.text = when (date.dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> "Пн"
                java.time.DayOfWeek.TUESDAY -> "Вт"
                java.time.DayOfWeek.WEDNESDAY -> "Ср"
                java.time.DayOfWeek.THURSDAY -> "Чт"
                java.time.DayOfWeek.FRIDAY -> "Пт"
                java.time.DayOfWeek.SATURDAY -> "Сб"
                java.time.DayOfWeek.SUNDAY -> "Вс"
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