package com.freedman.whatcanweeat.fragments.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.freedman.whatcanweeat.data.IngredientsDao
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.IngredientsAddItemBinding
import com.freedman.whatcanweeat.databinding.InstructionsAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.freedman.whatcanweeat.fragments.instructions.InstructionsAdapter
import com.freedman.whatcanweeat.tableDetails.Ingredients
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread

class IngredientsFragment(private val recipeInstruction: Recipe) : Fragment() {

    private lateinit var binding: RecyclerViewBinding
    private val ingredientsDao: IngredientsDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            requireContext()
        ).getIngredientsDao()
    }
    private var adapterIngredients = IngredientsAdapter() //this


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
        createIngredientList()
        setupFAB()
        binding.recyclerView.adapter = adapterIngredients

    }

    private fun setupFAB() {
        binding.fab.contentDescription = "Floating Action Bar For Adding Ingredients"
        binding.fab.setOnClickListener { showAddTaskDialogue() }
    }

    private fun showAddTaskDialogue() {
        val bindingIngredientsDialogue = IngredientsAddItemBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()) //reason for many addItemXML->so can change
        dialog.setContentView(bindingIngredientsDialogue.root)

        bindingIngredientsDialogue.buttonSaveIngredients.setOnClickListener {
            val recipeName = recipeInstruction.recipeName
            val ingredient =
                bindingIngredientsDialogue.editTextDialogueRecipeIngredients.text.toString()
            val optional = null
            thread {
                ingredientsDao.createIngredient(
                    Ingredients(
                        recipeName = recipeName,
                        ingredient = ingredient,
                        optional = optional
                    )
                )
                createIngredientList()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    fun createIngredientList() {
        thread {
            val ingredients = ingredientsDao.getIngredientsForRecipe(recipeInstruction.recipeName)
            requireActivity().runOnUiThread {
                adapterIngredients.setIngredients(ingredients)
            }
        }
    }
}


