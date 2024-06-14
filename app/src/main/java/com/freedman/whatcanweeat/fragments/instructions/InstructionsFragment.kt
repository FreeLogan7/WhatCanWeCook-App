package com.freedman.whatcanweeat.fragments.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.InstructionsAddItemBinding
import com.freedman.whatcanweeat.databinding.RecipesAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread

class InstructionsFragment(private val recipeInstruction: Recipe) : Fragment() {

    private lateinit var binding: RecyclerViewBinding
    private val instructionsDao: InstructionsDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            requireContext()
        ).getInstructionsDao()
    }
    private var adapterInstructions = InstructionsAdapter() //this


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
        createInstructionList()
        setupFAB()
        binding.recyclerView.adapter = adapterInstructions

    }

    private fun setupFAB() {
        binding.fab.contentDescription = "Floating Action Bar For Adding Instructions"
        binding.fab.setOnClickListener { showAddTaskDialogue() }
    }

    private fun showAddTaskDialogue() {
        val bindingInstructionsDialogue = InstructionsAddItemBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bindingInstructionsDialogue.root)

        bindingInstructionsDialogue.buttonSaveInstructions.setOnClickListener {
            val instructionRecipeName = recipeInstruction.recipeName
            val instructionDetails =
                bindingInstructionsDialogue.editTextDialogueRecipeInstruction.text.toString()

            //find ID
            thread {
                val maxID =
                    instructionsDao.getMaxInstructionIdForRecipe(recipeInstruction.recipeName)?: 0
                val instruction = Instructions(
                    recipeName = instructionRecipeName,
                    id = (maxID + 1),
                    instructions = instructionDetails,
                    image = null
                )
                instructionsDao.createInstructions(instruction)
                createInstructionList()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    fun createInstructionList() {
        thread {
            val instructions = instructionsDao.getInstructionsForRecipe(recipeInstruction.recipeName)
            requireActivity().runOnUiThread {
                adapterInstructions.setInstructions(instructions)
            }
        }
    }



}


