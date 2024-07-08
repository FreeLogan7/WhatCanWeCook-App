package com.freedman.whatcanweeat.fragments.instructions

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.R
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.InstructionsAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.freedman.whatcanweeat.fragments.groceries.GroceriesAdapter
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
        setupFragment()

        binding.recyclerViewCanMake.adapter = adapterInstructions


        setupSwipeToDelete(binding.recyclerViewCanMake, adapterInstructions)

    }



    private fun setupFragment() {
        binding.sectionLabelCanMake.visibility = View.GONE
        setupFAB()
    }

    private fun setupFAB() {
        binding.fabRecipe.contentDescription = "Floating Action Bar For Adding Instructions"
        binding.fabRecipe.setOnClickListener { showAddTaskDialogue() }
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

    private fun setupSwipeToDelete(recyclerView: RecyclerView, adapter: InstructionsAdapter) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val instructions = adapter.getInstructionAtPosition(position)
                    onInstructionDelete(instructions)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val background = ColorDrawable(Color.RED)
                    val deleteIcon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_delete_white_24dp
                    )!!
                    val intrinsicWidth = deleteIcon.intrinsicWidth
                    val intrinsicHeight = deleteIcon.intrinsicHeight

                    val itemHeight = itemView.bottom - itemView.top
                    val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                    val iconMargin = (itemHeight - intrinsicHeight) / 2
                    val iconLeft: Int
                    val iconRight: Int
                    if (dX > 0) { //if i want to swipe right
                        iconLeft = itemView.left + iconMargin
                        iconRight = itemView.left + iconMargin + intrinsicWidth
                        background.setBounds(
                            itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt(),
                            itemView.bottom
                        )
                    } else if (dX < 0) { //swiping left
                        iconLeft = itemView.right - iconMargin - intrinsicWidth
                        iconRight = itemView.right - iconMargin
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    } else {
                        // Clear the background
                        background.setBounds(0, 0, 0, 0)
                        iconLeft = 0
                        iconRight = 0
                    }

                    val iconBottom = iconTop + intrinsicHeight

                    // Draw the red background
                    background.draw(c)

                    // Draw the delete icon
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun onInstructionDelete(instructions:Instructions){
        thread {
            instructionsDao.deleteInstructions(instructions)
            createInstructionList()
        }
    }





}


