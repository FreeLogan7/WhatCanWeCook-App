


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.freedman.whatcanweeat.Activities.RecipeActivity
import com.freedman.whatcanweeat.data.IngredientsDao
import com.freedman.whatcanweeat.data.InstructionsDao
import com.freedman.whatcanweeat.data.RecipeDao
import com.freedman.whatcanweeat.data.WhatCanWeEatDatabase
import com.freedman.whatcanweeat.databinding.ActivityMainBinding
import com.freedman.whatcanweeat.fragments.groceries.GroceriesFragment
import com.freedman.whatcanweeat.fragments.recipes.RecipesFragment
import com.freedman.whatcanweeat.tableDetails.Ingredients
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() , RecipesFragment.NewActivityListener { //, RecipesFragment.ChangeTitle

    private lateinit var binding: ActivityMainBinding

    private val updatePreferences: SharedPreferences  by lazy {  this.getSharedPreferences("grocery-update", Context.MODE_PRIVATE)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFirstRun()

        updatePreferences.edit { putString("grocery", 0.toString()) }
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



    private fun getFirstRun() {
        val firstRunPreference: SharedPreferences = getSharedPreferences("first-run", Context.MODE_PRIVATE)
        val isFirstRun = firstRunPreference.getBoolean("isFirstRunTime7", true)

        if (isFirstRun) {
            addStarterRecipes()

            firstRunPreference.edit(){
                putBoolean("isFirstRunTime7", false)
                apply()
            }
        }
    }

    private fun addStarterRecipes() {
        val recipeDao: RecipeDao = WhatCanWeEatDatabase.getDatabase(this).getRecipeDao()
        val instructionsDao: InstructionsDao = WhatCanWeEatDatabase.getDatabase(this).getInstructionsDao()
        val ingredientsDao: IngredientsDao = WhatCanWeEatDatabase.getDatabase(this).getIngredientsDao()




thread {
        recipeDao.createRecipe(Recipe(recipeName = "Lemongrass and Coconut Chicken", description = "Tender chicken in a fragrant lemongrass and creamy coconut sauce"))
        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 1, instructions = """Topic cooked rice:
•Before starting, wash and dry all produce
•Using a strainer, rinse rice until water runs clear.
•Add one cup water to a medium pot. Cover and bring to a boil over high heat.
•Once boiling, add rice and half the garlic salt, then reduce heat to a medium-low
•Cover and cook until rice is tender and liquid is absorbed, 12 to 14 minutes.
•Remove from heat. Set aside, still covered.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 2, instructions = """Topic prep
•Meanwhile, remove outer layer of lemon grass, then cut into quarters crosswise. Using the back of a wooden spoon, forcefully tap lemongrass to bruise.
•Core, then cut pepper into 1/4 inch slices.
•Peel, then cut half the carrot into 1/8 inch matchsticks. 
•Zest, then juice, half the lime. Cut any remaining lime into wedges.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 3, instructions = """Topic, sear chicken
•Meanwhile, pat chicken dry with paper towels. Cut into 1 inch pieces. Season with remaining garlic salt and pepper.
•Heat a large nonstick pan over medium high heat.
•When hot, add half tablespoon oil, then chicken. Pan fry until golden Brown, 2 to 3 minutes per side.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 4, instructions = """Topic start lemongrass sauce
•Add lemongrass. Cook, stirring often, until fragrant, 1 minute.
•Add coconut milk, curry paste, soy sauce, 1/4 cup water and 2 teaspoon sugar. Stir to combine, then bring to a boil over high.
•Once boiling, add carrots and peppers.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 5, instructions = """Topic finish lemon grass sauce
•Reduce heat to medium-low, then cover and cook until chicken is cooked through, 4 to 5 minutes
•Carefully remove Grass from the pan and discard.
•Stir in lime juice. Season with salt and pepper, to taste.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Lemongrass and Coconut Chicken",id = 6, instructions = """Topic finish and serve
•Fluff rice with a fork, then stir in lime zest. Season with salt, to taste.
•Rice bowls. Top with lemongrass, chicken, veggies, and sauce.
•Squeeze a lime wedge over top if desired.
        """.trimIndent(), null))

        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Chicken tenders", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Basmati rice", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Coconut milk", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Soy sauce", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Red curry paste", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Carrot", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Lime", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Garlic salt", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Sweet bell pepper", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Lemon grass", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Salt", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Pepper", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Sugar", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Lemongrass and Coconut Chicken",ingredient = "Oil", null))



        recipeDao.createRecipe(Recipe(recipeName = "Creamy Squash Ravioli", description = "Ravioli filled with creamy squash, offering a rich and savory flavor"))
        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 1, instructions = """Topic prep and cook mushrooms
•Before starting, add 10 cups water and 1 tablespoon salt to a large pot
•Cover and bring it to a boil over high heat.
•Wash and dry all produce.
•Heat a large nonstick pan over medium to high heat.
•While the pan heats, thinly sliced mushrooms.
•When the pan is hot, add 2 tablespoon butter, then swirl melted, one minute
•Add mushrooms. Cook, stirring occasionally, until golden brown, 5 to 6 minutes.
•Season with salt and pepper.
•Add cooking wine. Cook, stirring often, until absorbed, one minute.      
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 2, instructions = """Make sauce
•Sprinkle flour over mushrooms. Cook, stirring often, until mushrooms are coated, 1 min.
•Add cream, broth concentrate and 1/4 cup (1⁄2 cup) water or milk. Bring to a simmer, then reduce heat to low.
•Cook, stirring occasionally, until sauce thickens slightly, 2-3 min.
•Remove from heat, then cover to keep warm.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 3, instructions = """Cook ravioli
•While spinach wilts, add ravioli to the boiling water. Cook, stirring gently, until tender, 2-4 min.
•Reserve 1⁄4 cup (1⁄2 cup) pasta water, then drain.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 4, instructions = """Finish sauce
•Add spinach and half the Parmesan to the pan with sauce.
•Season with salt and pepper, then stir until spinach wilts, 1 min. (TIP: For a lighter sauce consistency, add reserved pasta water,
•1-2 tbsp at a time, if desired.)
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 5, instructions = """Finish and serve
•Divide ravioli between bowls.
•Top with mushroom sauce.
•Sprinkle remaining Parmesan over top.
        """.trimIndent(), null))

        instructionsDao.createInstructions(Instructions(recipeName = "Creamy Squash Ravioli",id = 6, instructions = """Vacuum-pack guarantees maximum freshness but can lead to small colour changes and a stronger scent. Both will disappear 3 minutes after opening.
**Cook to a minimum internal temperature of 74°C/165°F, as size may vary.
        """.trimIndent(), null))

        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Squash ravioli", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Mushroom", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Baby spinach", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Shredded parmesan cheese", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Vegetable broth concentrate", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "All purpose flour", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Cream", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "White cooking wine", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Unsalted butter", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Salt", null))
        ingredientsDao.createIngredient(Ingredients(recipeName = "Creamy Squash Ravioli",ingredient = "Pepper", null))


    }
}
    }

