package com.freedman.whatcanweeat.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.freedman.whatcanweeat.data.IngredientsDao
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.tableDetails.Ingredients
import com.freedman.whatcanweeat.tableDetails.Recipe
import kotlin.concurrent.thread


class AddRecipes() : AppCompatActivity() {

    private val recipeDao: RecipeDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            this
        ).getRecipeDao()
    }

    private val ingredientsDao: IngredientsDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            this
        ).getIngredientsDao()
    }

    private val instructionsDao: InstructionsDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            this
        ).getInstructionsDao()
    }

    fun createDefaultRecipes() {
        thread {
        recipeDao.createRecipe(Recipe("Lemongrass and Coconut Chicken", "with Lime Basmati Rice"))
        recipeDao.createRecipe(Recipe("Creamy Squash Ravioli", "with Mushrooms and Spinach"))
        recipeDao.createRecipe(Recipe("Pan Seared Pork Chops", "with Shallot Gravy, Fresh Salad and Buttery Mash"))
        recipeDao.createRecipe(Recipe("Zesty Black Bean Taquitos", "with Monterey Jack Cheese and Fresh Salad"))
        }
    }

    fun createDefaultIngredients() {
        //LemonGrass and Coconut Chicken
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Chicken Tenders",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Basmati Rice",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Coconut Milk",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Soy Sauce",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Red Curry Paste",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Carrot",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Lime",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Garlic Salt",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Sweet Bell Pepper",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Lemon Grass",false))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Salt",true))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Pepper",true))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Sugar",true))
        ingredientsDao.createIngredient(Ingredients("Lemongrass and Coconut Chicken", "Oil",true))

        //Creamy Squash Ravioli





    }

    fun createDefaultInstructions() {

    }
}