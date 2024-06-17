package com.freedman.whatcanweeat.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.freedman.whatcanweeat.data.IngredientsDao
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase


class AddRecipes () : AppCompatActivity() {

    private val ingredientsDao: IngredientsDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            this
        ).getIngredientsDao()
    }

    fun createDefaultRecipes(){

    }

    fun createDefaultIngredients(){

    }

    fun createDefaultInstructions(){

    }
}