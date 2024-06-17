package com.freedman.whatcanweeat.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.ActivityRecipeBinding
import com.freedman.whatcanweeat.databinding.InstructionsAddItemBinding
import com.freedman.whatcanweeat.fragments.ingredients.IngredientsFragment
import com.freedman.whatcanweeat.fragments.instructions.InstructionsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.concurrent.thread

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeBinding
    private val recipeInstruction: Recipe by lazy { intent.getSerializableExtra("recipe-instruction") as Recipe }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pagerInstructions.adapter = PagerAdapterInstruction(this)

        setPageName()
        setupTabs()
        setupBackButton()
    }

    private fun setupBackButton() {
        setSupportActionBar(binding.toolbarInstructions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarInstructions.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupTabs() {

        TabLayoutMediator(binding.tabLayoutInstructions, binding.pagerInstructions) { tab, i ->
            when (i) {
                0 -> tab.text = "Instructions"
                1 -> tab.text = "Ingredients"
            }
        }.attach()
    }

    private fun setPageName() {
        binding.toolbarInstructions.title = recipeInstruction.recipeName
    }

    inner class PagerAdapterInstruction(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
             return when (position) {
                0 -> InstructionsFragment(recipeInstruction)
                else -> IngredientsFragment(recipeInstruction)
            }
        }
    }


    override fun onBackPressed() { super.onBackPressed() }



}
