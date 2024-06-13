package com.freedman.whatcanweeat.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.freedman.whatcanweeat.STARTER_RECIPES
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.ActivityMainBinding
import com.freedman.whatcanweeat.databinding.RecipesAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread

class RecipesFragment(
    private val titleChanger: ActivityMainBinding,
    private val listener: NewActivityListener
) : Fragment(), RecipeAdapter.SendMeToRecipeListener {

    private lateinit var binding: RecyclerViewBinding
    private var favourite = false
    private val recipeDao: RecipeDao by lazy {
        WhatCanWeEatDatabase.getDatabase(requireContext()).getRecipeDao()
    }

    private var adapter = RecipeAdapter(this)

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
        binding.fab.setOnClickListener { showAddTaskDialogue() }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2, LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        titleChanger.toolbar.title = "Recipes"
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
            val recipeInputName = bindingRecipeDialogue.editTextDialogueRecipeName.text.toString()
            val recipeInputDescription = bindingRecipeDialogue.editTextDialogueRecipeDescription.text.toString()

            val recipe = Recipe(recipeName = recipeInputName, description = recipeInputDescription, favourite = favourite)

            thread { recipeDao.createRecipe(recipe) }
            dialog.dismiss()
            createRecipeList()
        }
        dialog.show()
    }



    fun createRecipeList() {
        thread {
            val recipes = recipeDao.getAllRecipes()
            requireActivity().runOnUiThread {
                 adapter.setRecipes(recipes)
            }
        }
    }

    fun canWeMakeIt(){
        //Use database
        //Find
    }

    override fun recipeSendOff(recipe: Recipe) {
        listener.newActivity(recipe)
    }

    interface NewActivityListener {
        fun newActivity(recipe: Recipe)
    }


}





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


