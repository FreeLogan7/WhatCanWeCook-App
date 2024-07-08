package com.freedman.whatcanweeat.fragments.recipes

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
    private var adapterCanMake = RecipeAdapter( this)
    private var adapterCanNotMake = RecipeAdapter( this)
    private var allRecipes : List<Recipe> = listOf()
    private var recipesInFridge : List<Recipe> = listOf()
    private var recipesNotInFridge : List<Recipe> = listOf()
    private var showNotInFridge : Int = 1


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
        binding.recyclerViewCanMake.adapter = adapterCanMake

        binding.recyclerViewCanNotMake.layoutManager = GridLayoutManager(requireContext(),2, LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewCanNotMake.adapter = adapterCanNotMake
        binding.sectionLabelCanNotMake.visibility = View.VISIBLE

        setNotInFridgeClicker()

    }

    private fun setNotInFridgeClicker() {
        binding.sectionLabelCanNotMake.setOnClickListener{
            when (showNotInFridge) {
                1 -> {
                    showNotInFridge = 0
                    binding.recyclerViewCanNotMake.visibility = View.GONE
                }

                0 -> {
                    showNotInFridge = 1
                    binding.recyclerViewCanNotMake.visibility = View.VISIBLE
                }
            }
        }
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
            allRecipes = recipesInFridge + recipesNotInFridge
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

    private fun createRecipeList(){
        createRecipeListInFridge()
    }

    private fun createRecipeListInFridge(){
        thread {
            val recipeNamesInFridge = recipeDao.getInFridgeRecipeNames()
            val recipesInFridge = recipeDao.getInFridgeRecipes(recipeNamesInFridge)
            requireActivity().runOnUiThread {
                this.recipesInFridge = recipesInFridge
                adapterCanMake.setRecipes(recipesInFridge, GREEN_COLOR)
                when (showNotInFridge){
                    1 -> createRecipeListNotInFridge(recipeNamesInFridge)
                    0 -> {}
                }
            }
        }
    }

    private fun createRecipeListNotInFridge(recipeNamesInFridge: List<String>) {
        thread {
            val recipesNotInFridge = recipeDao.getNotInFridgeRecipes(recipeNamesInFridge)
            requireActivity().runOnUiThread {
                this.recipesNotInFridge = recipesNotInFridge
                adapterCanNotMake.setRecipes(recipesNotInFridge, FADED_COLOR)
            }
        }
    }



    override fun recipeSendOff(recipe: Recipe) {
        listener.newActivity(recipe)
    }

    override fun deleteRecipe(recipe: Recipe) {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {thread {
                        recipeDao.deleteRecipe(recipe) }
                        createRecipeList()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {}
                }
            }
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()


    }

    interface NewActivityListener {
        fun newActivity(recipe: Recipe)
    }

    companion object{
        const val GREEN_COLOR = 0xFFACF89B.toInt()
        const val FADED_COLOR = 0xFFB6B6B6.toInt()
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


