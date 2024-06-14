package com.freedman.whatcanweeat.fragments.groceries

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.ErrorCheck.OnAddErrorCheck
import com.freedman.whatcanweeat.R
import com.freedman.whatcanweeat.data.GroceryDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.ActivityMainBinding
import com.freedman.whatcanweeat.databinding.GroceriesAddItemBinding
import com.freedman.whatcanweeat.databinding.RecyclerViewGroceriesBinding
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment.Companion.FADED_COLOR
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment.Companion.GREEN_COLOR
import com.freedman.whatcanweeat.tableDetails.Groceries
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread


class GroceriesFragment(private val titleChanger: ActivityMainBinding) : Fragment(),
    GroceriesAdapter.GroceryUpdateListener, OnAddErrorCheck {

    private lateinit var binding: RecyclerViewGroceriesBinding
    private val groceryDao: GroceryDao by lazy {
        WhatCanWeEatDatabase.getDatabase(requireContext()).getGroceryDao()
    }
    private var adapterInFridge = GroceriesAdapter(this)
    private var adapterNotInFridge = GroceriesAdapter(this)

    private var inFridgeGroceries : List<Groceries> = listOf()
    private var notInFridgeGroceries : List<Groceries> = listOf()

    private var allGroceries : List<Groceries> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecyclerViewGroceriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewGroceriesInFridge.adapter = adapterInFridge
        binding.recyclerViewGroceriesNotInFridge.adapter = adapterNotInFridge

        getAllGroceries()
        binding.fab.setOnClickListener { showAddTaskDialogue() }

        setupSwipeToDelete(binding.recyclerViewGroceriesInFridge, adapterInFridge)
        setupSwipeToDelete(binding.recyclerViewGroceriesNotInFridge, adapterNotInFridge)
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView, adapter: GroceriesAdapter) {
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
                    val grocery = adapter.getGroceryAtPosition(position)
                    onGroceryDelete(grocery)
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

    private fun showAddTaskDialogue() {
        val bindingDialogue = GroceriesAddItemBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bindingDialogue.root)

        bindingDialogue.buttonSave.setOnClickListener {
            val groceryInputNameRAW = bindingDialogue.editTextGroceryItem.text.toString()
            val groceryInputName = updateCapitalization(groceryInputNameRAW)

            //IMAGE: Here is where it would be added
            //add it to the grocery line -> if statement and changes made to adapter
            val grocery = Groceries(groceryName = groceryInputName, inFridge = true)

            allGroceries = inFridgeGroceries +notInFridgeGroceries
            onAddGroceryName(
                groceryDao = groceryDao,
                activity = requireActivity(),
                allGroceries = allGroceries,
                grocery = grocery,
                groceryInputName = groceryInputName,
                context = requireContext()
            )

            dialog.dismiss()
            getInFridgeGroceries()
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        titleChanger.toolbar.title = "Groceries"
    }

    fun getAllGroceries() {
        getInFridgeGroceries()
        getNotInFridgeGroceries()
    }

    fun getInFridgeGroceries() {
        thread {
            val groceriesInFridge = groceryDao.getInFridgeGroceries()
            requireActivity().runOnUiThread {
                this.inFridgeGroceries = groceriesInFridge
                adapterInFridge.setGroceries(groceriesInFridge, GREEN_COLOR)

            }
        }
    }

    fun getNotInFridgeGroceries() {
        thread {
            val groceriesNotInFridge = groceryDao.getNotInFridgeGroceries()
            requireActivity().runOnUiThread {
                this.notInFridgeGroceries = groceriesNotInFridge
                binding.inFridgeSectionLabel.visibility =
                    if (groceriesNotInFridge.isEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                adapterNotInFridge.setGroceries(groceriesNotInFridge, FADED_COLOR)
            }
        }
    }

    fun updateGroceriesAfterPageSwap()
    {
        val updatePreferences: SharedPreferences = requireContext().getSharedPreferences("grocery-update", Context.MODE_PRIVATE)
        updatePreferences.edit{
            putString("grocery", 1.toString())
        }
    }

    override fun onGroceryUpdate(grocery: Groceries) {
        updateGroceriesAfterPageSwap()
        thread {
            groceryDao.updateGrocery(grocery)
            getAllGroceries()

        }
    }

    override fun onGroceryDelete(grocery: Groceries) {
        updateGroceriesAfterPageSwap()
        thread {
            groceryDao.deleteGrocery(grocery)
            getAllGroceries()
        }
    }

}

