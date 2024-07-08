package com.freedman.whatcanweeat.fragments.ingredients

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.R
import com.freedman.whatcanweeat.data.GroceryDao
import com.freedman.whatcanweeat.data.IngredientsDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.IngredientsAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewBinding
import com.freedman.whatcanweeat.fragments.instructions.InstructionsAdapter
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment.Companion.FADED_COLOR
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment.Companion.GREEN_COLOR
import com.freedman.whatcanweeat.tableDetails.Ingredients
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
    private val groceryDao: GroceryDao by lazy {
        WhatCanWeEatDatabase.getDatabase(
            requireContext()
        ).getGroceryDao()
    }
    private var adapterIngredients = IngredientsAdapter() //this
    private var adapterDoNotHaveIngredients = IngredientsAdapter()


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
        setupFragment()
        binding.recyclerViewCanMake.adapter = adapterIngredients
        binding.recyclerViewCanNotMake.adapter = adapterDoNotHaveIngredients
        setupSwipeToDelete( binding.recyclerViewCanMake,adapterIngredients)

    }

    private fun setupFragment() {
        binding.sectionLabelCanMake.visibility = View.GONE
        setupFAB()
    }

    private fun updater() {
        val updatePreferences: SharedPreferences = requireContext().getSharedPreferences("grocery-update", Context.MODE_PRIVATE)
        updatePreferences.edit{putString("grocery", 1.toString())}
    }

    private fun setupFAB() {
        binding.fabRecipe.contentDescription = "Floating Action Bar For Adding Ingredients"
        binding.fabRecipe.setOnClickListener { showAddTaskDialogue() }
    }

    private fun showAddTaskDialogue() {
        val bindingIngredientsDialogue = IngredientsAddItemBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()) //reason for many addItemXML->so can change
        dialog.setContentView(bindingIngredientsDialogue.root)

        bindingIngredientsDialogue.buttonSaveIngredients.setOnClickListener {
            updater()
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

    private fun createIngredientList(){
        createIngredientListInFridge()
        createIngredientListNotInFridge()
    }
//only need to re-check ingredientsList verse Grocery List when Grocery List has been updated
    //but we will do the check everytime for now
private fun createIngredientListInFridge() {
        thread {
            //val ingredients = ingredientsDao.getInFridgeIngredientNames(recipeInstruction.recipeName)
            val groceriesInFridge = groceryDao.getInFridgeGroceriesNames()
            val ingredientsInFridge = ingredientsDao.getInFridgeIngredients(recipeInstruction.recipeName,  groceriesInFridge)
            if(ingredientsInFridge.isNotEmpty()){
                requireActivity().runOnUiThread {
                    adapterIngredients.setIngredients(ingredientsInFridge, GREEN_COLOR) //add color
            }
            }
        }
    }

    private fun createIngredientListNotInFridge() {
        thread {
            //val ingredientsNot = ingredientsDao.getInFridgeIngredientNames(recipeInstruction.recipeName)
            val groceriesInFridge = groceryDao.getInFridgeGroceriesNames()
            val ingredientsNotInFridge = ingredientsDao.getNotInFridgeIngredients(recipeInstruction.recipeName, groceriesInFridge)
            if (ingredientsNotInFridge.isNotEmpty()){
                requireActivity().runOnUiThread {
                    adapterDoNotHaveIngredients.setIngredients(ingredientsNotInFridge, FADED_COLOR) //add color
                }
            }

        }
    }



    private fun setupSwipeToDelete(recyclerView: RecyclerView, adapter: IngredientsAdapter) {
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
                    val ingredients = adapter.getIngredientsAtPosition(position)
                    onIngredientsDelete(ingredients)
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

    fun onIngredientsDelete(ingredients: Ingredients){
        thread {
            ingredientsDao.deleteIngredients(ingredients)
            createIngredientList()
        }

    }
}


//fun createIngredientListInFridge() {
//    thread {
//        val ingredients = ingredientsDao.getIngredientsForRecipe(recipeInstruction.recipeName)
//        requireActivity().runOnUiThread {
//            adapterIngredients.setIngredients(ingredients)
//        }
//    }
//}