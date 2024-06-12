package com.freedman.whatcanweeat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.freedman.whatcanweeat.Activities.RecipeActivity
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.databinding.ActivityMainBinding
import com.freedman.whatcanweeat.fragments.groceries.GroceriesFragment
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayoutMediator

const val STARTER_RECIPES: Int = 4

class MainActivity : AppCompatActivity() , RecipesFragment.NewActivityListener { //, RecipesFragment.ChangeTitle

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Temporary line

        binding.pager.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, i ->
            when (i) {
                0 -> tab.text = "Recipes"
                1 -> tab.text = "Groceries"
            }
        }.attach()
    }


    inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RecipesFragment(binding, this@MainActivity)
                else -> GroceriesFragment(binding)
            }
        }
    }

    override fun newActivity(recipe: Recipe) {
       val intent = Intent(this, RecipeActivity::class.java)
        startActivity(intent.apply { putExtra("recipe-instruction", recipe) })
    }

}