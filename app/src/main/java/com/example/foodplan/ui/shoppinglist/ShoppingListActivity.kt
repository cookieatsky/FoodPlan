package com.example.foodplan.ui.shoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodplan.databinding.ActivityShoppingListBinding
import com.example.foodplan.model.ShoppingListItem
import com.example.foodplan.repository.ShoppingListRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var repository: ShoppingListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = ShoppingListRepository.getInstance(this)

        setupRecyclerView()
        observeShoppingList()
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            onItemChecked = { item ->
                lifecycleScope.launch {
                    repository.updateItem(item)
                }
            },
            onItemDelete = { item ->
                lifecycleScope.launch {
                    repository.deleteItem(item)
                }
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ShoppingListActivity)
            adapter = this@ShoppingListActivity.adapter
        }
    }

    private fun observeShoppingList() {
        lifecycleScope.launch {
            repository.getAllItems().collectLatest { items ->
                adapter.submitList(items)
            }
        }
    }
} 