package com.freedman.whatcanweeat.fragments.recipes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.freedman.whatcanweeat.ErrorCheck.OnAddErrorCheck
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.ActivityMainBinding
import com.freedman.whatcanweeat.databinding.RecipesAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread

class RecipesFragment(
    private val titleChanger: ActivityMainBinding,
    private val listener: NewActivityListener
) : Fragment(), RecipeAdapter.SendMeToRecipeListener, OnAddErrorCheck {

    private lateinit var binding: RecyclerViewBinding
    private var favourite = false
    private val recipeDao: RecipeDao by lazy {
        WhatCanWeEatDatabase.getDatabase(requireContext()).getRecipeDao()
    }



    private var adapter = RecipeAdapter( this)
    private var allRecipes : List<Recipe> = listOf()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecyclerViewBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecipeList()



        binding.fabRecipe.setOnClickListener { showAddTaskDialogue() }
        binding.recyclerViewCanMake.layoutManager = GridLayoutManager(requireContext(),2, LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewCanMake.adapter = adapter



        //CREATE 1 XML, 3 RECYCLER VIEW
        //CREATE 3 createRecipe FUNCTIONS WITH DIFFERENT COLORS
        //CHANGE ADAPTER TO INCLUDE COLOR

    }

    override fun onResume() {
        super.onResume()
        titleChanger.toolbar.title = "Recipes"
        updater()
    }

    private fun updater() {
        val updatePreferences: SharedPreferences = requireContext().getSharedPreferences("grocery-update", Context.MODE_PRIVATE)
        val updater = updatePreferences.getString("grocery", null)!!.toInt()
        if (updater == 1){
            createRecipeList()
            updatePreferences.edit{putString("grocery", 0.toString())}
        }
    }

    private fun showAddTaskDialogue() {
        val bindingRecipeDialogue = RecipesAddItemBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bindingRecipeDialogue.root)

        bindingRecipeDialogue.buttonShowDescription.setOnClickListener {
            bindingRecipeDialogue.editTextDialogueRecipeDescription.visibility =
                if (bindingRecipeDialogue.editTextDialogueRecipeDescription.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        bindingRecipeDialogue.buttonStarFavourite.setOnClickListener{
            bindingRecipeDialogue.buttonStarFavouriteFilled.visibility =
                if (bindingRecipeDialogue.buttonStarFavouriteFilled.visibility == View.GONE)
                {
                    favourite = true
                    View.VISIBLE

                } else {
                    favourite = false
                    View.GONE
                }
        }

        bindingRecipeDialogue.buttonSave.setOnClickListener {
            val recipeInputNameRAW = bindingRecipeDialogue.editTextDialogueRecipeName.text.toString()
            val recipeInputName = updateCapitalization(recipeInputNameRAW)
            val recipeInputDescription = bindingRecipeDialogue.editTextDialogueRecipeDescription.text.toString()

            val recipe = Recipe(recipeName = recipeInputName, description = recipeInputDescription, favourite = favourite)

            onAddRecipeName(recipeDao = recipeDao,
                activity = requireActivity(),
                allRecipes = allRecipes,
                recipe = recipe,
                recipeInputName = recipeInputName,
                context = requireContext() )

            dialog.dismiss()
            createRecipeList()
        }
        dialog.show()
    }

    fun createRecipeList(){
        createRecipeListInFridge()
        //createRecipeListNotInFridge()
    }

    fun createRecipeListInFridge(){
        thread {
            val recipeNamesInFridge = recipeDao.getInFridgeRecipeNames()
            val recipesInFridge = recipeDao.getInFridgeRecipes(recipeNamesInFridge)
            requireActivity().runOnUiThread {
                this.allRecipes = recipesInFridge
                adapter.setRecipes(recipesInFridge, GREEN_COLOR)
            }
        }
    }



    override fun recipeSendOff(recipe: Recipe) {
        listener.newActivity(recipe)
    }

    interface NewActivityListener {
        fun newActivity(recipe: Recipe)
    }

    companion object{
        const val GREEN_COLOR = 0xFFACF89B.toInt()
        const val WHITE_COLOR = 0xFFFFFFFF.toInt()
        const val FADED_COLOR = 0x99B6B6B6.toInt()
    }


}




/*fun createRecipeListNotInFridge(){
    thread {
        val recipesInFridge = recipeDao.getNotInFridgeRecipes()
        requireActivity().runOnUiThread {
            adapter.setRecipes(recipesInFridge, FADED_COLOR)
        }
    }
}*/



//    fun createRecipeList() { //: List<Recipe> //MAKE RECIPES A VALUE THAT CAN BE SENT ELSEWHERE
//        thread {
//            val recipes = recipeDao.getAllRecipes()
//            requireActivity().runOnUiThread {
//            //return recipes
//            adapter.setRecipes(recipes)
//            }
//        }
//    }

//    fun createRecipeList(callback: (List<Recipe>) -> Unit) {
//        thread {
//            val recipes = recipeDao.getAllRecipes()
//            requireActivity().runOnUiThread {
//                callback(recipes)
//            }
//        }
//    }
//    createRecipeList { recipes ->
//        // This runs on the UI thread
//        adapter.setRecipes(recipes)
//    }

//fun canWeMakeIt(){
//    //Use database
//    //Find
//}


//    private fun createStarterRecipes() {
//        thread {
//            val areThereRecipes = recipeDao.getAllRecipes()
//            if (areThereRecipes.size < STARTER_RECIPES) {
//                recipeDao.createRecipe(Recipe(recipeName = "Cereal", description = "milky carbs"))
//                recipeDao.createRecipe(Recipe(recipeName = "Ham Sandwhich", description = "Hammy Love"))
//                recipeDao.createRecipe(Recipe(recipeName = "Cheese", description = "It's just Cheese"))
//                recipeDao.createRecipe(Recipe(recipeName = "Bipity", description = "Bop"))
//
//                instructionsDao.createInstructions(Instructions(recipeName = "Cereal", id = 1, instructions = "Pour Milk into Bowl",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Cereal", id = 2, instructions = "Pour Cereal into same Bowl",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Cereal", id = 3, instructions = "You are also now on a list for being a Sociopath",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Ham Sandwhich", id = 1, instructions = "Cut up Tomatoes and Ham",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Ham Sandwhich", id = 2, instructions = "Put everything in Bread",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Cheese", id = 1, instructions = "It's just cheese",image = null))
//                instructionsDao.createInstructions(Instructions(recipeName = "Bipity", id = 1, instructions = "Don't be surprised",image = null))
//
//            }
//        }
//    }


