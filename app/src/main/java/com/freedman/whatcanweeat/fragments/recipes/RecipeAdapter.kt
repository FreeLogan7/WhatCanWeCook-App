package com.freedman.whatcanweeat.fragments.recipes


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.databinding.ItemRecipesBinding

class RecipeAdapter( private val listener: SendMeToRecipeListener) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = listOf()

    override fun getItemCount() = recipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bindingRecipe =
            ItemRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingRecipe)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecipes (recipe: List<Recipe>){
        this.recipes = recipe
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    inner class ViewHolder(private val binding: ItemRecipesBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(recipe: Recipe){ //here you can also send position, and it would keep track
                binding.textViewTitle.text = recipe.recipeName
                binding.textViewDescription.text = recipe.description
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
    }

}


