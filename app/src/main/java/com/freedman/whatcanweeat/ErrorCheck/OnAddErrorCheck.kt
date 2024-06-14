package com.freedman.whatcanweeat.ErrorCheck

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.freedman.whatcanweeat.data.GroceryDao
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.tableDetails.Groceries
import com.freedman.whatcanweeat.tableDetails.Recipe
import kotlin.concurrent.thread

interface OnAddErrorCheck {

    fun updateCapitalization (inputRAW: String): String{
        return inputRAW.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun onAddRecipeName(
        recipeDao: RecipeDao,
        activity: FragmentActivity,
        allRecipes: List<Recipe>,
        recipe: Recipe,
        recipeInputName: String,
        context: Context
    ) {
        thread {
            val exists = allRecipes.any() { it.recipeName == recipeInputName }
            if (!exists) {
                recipeDao.createRecipe(recipe)
            } else {
                activity.runOnUiThread {
                    Toast.makeText(context, "Recipe item already exists", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun onAddGroceryName(
        groceryDao: GroceryDao,
        activity: FragmentActivity,
        allGroceries: List<Groceries>,
        grocery: Groceries,
        groceryInputName: String,
        context: Context
    ) {
        thread {
            val exists = allGroceries.any() { it.groceryName == groceryInputName }
            if (!exists) {
                groceryDao.createGrocery(grocery)
            } else {
                activity.runOnUiThread {
                    Toast.makeText(context, "Grocery item already exists", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

    }
}