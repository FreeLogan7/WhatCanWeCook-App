package com.freedman.whatcanweeat.fragments.recipes


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.R
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.databinding.ItemRecipesBinding

class RecipeAdapter(  private val listener: SendMeToRecipeListener) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = listOf()
    private var color: Int = 0



    override fun getItemCount() = recipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bindingRecipe =
            ItemRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingRecipe)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecipes (recipe: List<Recipe>, color: Int){
        this.recipes = recipe
        this.color = color
       notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    inner class ViewHolder(private val binding: ItemRecipesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) { //here you can also send position, and it would keep track
            binding.cardViewItem.setCardBackgroundColor(color)
            binding.textViewTitle.text = recipe.recipeName
            binding.textViewDescription.text = recipe.description
            binding.cardViewItem.setOnLongClickListener() { listener.deleteRecipe(recipe)
                return@setOnLongClickListener true }

            binding.cardViewItem.setOnClickListener(){listener.recipeSendOff(recipe)}
                //add image or add if it is a favourite here by adding a isChecked
//                binding.toggleStar.setOnClickListener {
//                    val updatedStarTask = task.copy(isStarred = binding.toggleStar.isChecked)
//                    listener.onTaskUpdated(updatedStarTask)
//                }
            }

    }

    interface SendMeToRecipeListener {
        fun recipeSendOff(recipe: Recipe)
        fun deleteRecipe(recipe: Recipe)
    }

}




