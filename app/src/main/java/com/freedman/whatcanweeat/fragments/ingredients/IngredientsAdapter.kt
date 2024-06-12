package com.freedman.whatcanweeat.fragments.ingredients


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.databinding.ItemIngredientsBinding
import com.freedman.whatcanweeat.tableDetails.Ingredients

class IngredientsAdapter() :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() { //private val listener: SendMeToInstructionsListener

    private var ingredients: List<Ingredients> = listOf()

    override fun getItemCount() = ingredients.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bindingIngredients =
            ItemIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingIngredients)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIngredients(ingredients: List<Ingredients>) {
        val sortedIngredients = ingredients.sortedBy {it.ingredient}
        this.ingredients = sortedIngredients
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    inner class ViewHolder(private val binding: ItemIngredientsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredients: Ingredients) {
            binding.textViewTitleIngredients.text = ingredients.ingredient
            //Later add amount of ingredient
        }
    }

}


