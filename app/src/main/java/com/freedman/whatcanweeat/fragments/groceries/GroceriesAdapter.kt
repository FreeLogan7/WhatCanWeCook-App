package com.freedman.whatcanweeat.fragments.groceries


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.tableDetails.Groceries
import com.freedman.whatcanweeat.databinding.ItemGroceriesBinding


class GroceriesAdapter(private val listener: GroceryUpdateListener) :
    RecyclerView.Adapter<GroceriesAdapter.ViewHolder>() {

    private var groceries: List<Groceries> = listOf()

    override fun getItemCount() = groceries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bindingGroceries =
            ItemGroceriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingGroceries)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groceries[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setGroceries(groceries: List<Groceries>) {
        val sortedGroceries = groceries.sortedBy { it.groceryName.uppercase() }.sortedByDescending { it.inFridge }
        this.groceries = sortedGroceries
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemGroceriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groceries: Groceries) {
            binding.textViewTitleGroceries.text = groceries.groceryName
            binding.checkboxGroceries.isChecked = groceries.inFridge
            binding.checkboxGroceries.setOnClickListener {
                val updatedGrocery = groceries.copy(inFridge = binding.checkboxGroceries.isChecked)
                listener.onGroceryUpdate(updatedGrocery)
            }
        }
    }

    fun getGroceryAtPosition(position: Int): Groceries {
        return groceries[position]
    }


    interface GroceryUpdateListener {
        fun onGroceryUpdate(grocery: Groceries)
        fun onGroceryDelete(grocery: Groceries)
    }

}
