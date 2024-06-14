package com.freedman.whatcanweeat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.freedman.whatcanweeat.tableDetails.Groceries
import com.freedman.whatcanweeat.tableDetails.Ingredients
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.tableDetails.Recipe

@Dao
interface RecipeDao {

    @Insert
    fun createRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): List<Recipe>

    @Update
    fun updateRecipe (recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Query("""
        SELECT recipe_ingredients.recipe_name
from recipe_ingredients
left join groceries on recipe_ingredients.ingredient = groceries.grocery_name
group by recipe_name
having SUM(CASE WHEN groceries.grocery_name is null OR groceries.inFridge is 0 THEN 1  ELSE 0 END) = 0;

    """)
    fun getInFridgeRecipeNames(): List<String>


    @Query("SELECT * FROM recipe WHERE recipe.recipe_name IN (:recipeNames)")
    fun getInFridgeRecipes(recipeNames: List<String>): List<Recipe>
}



@Dao
interface GroceryDao {

    @Insert
    fun createGrocery(groceries: Groceries)

    @Query("SELECT * FROM groceries")
    fun getAllGroceries(): List<Groceries>

    @Query("SELECT * FROM groceries WHERE inFridge = 1")
    fun getInFridgeGroceries(): List<Groceries>

    @Query("SELECT * FROM groceries WHERE inFridge = 0")
    fun getNotInFridgeGroceries(): List<Groceries>

    @Update
    fun updateGrocery(groceries: Groceries)

    @Delete
    fun deleteGrocery(groceries: Groceries)

}


@Dao
interface InstructionsDao {

    @Insert
    fun createInstructions(instructions: Instructions)

    @Query("SELECT * FROM instructions")
    fun getAllInstructions(): List<Instructions>

    @Update
    fun updateInstructions (instructions: Instructions)

    @Delete
    fun deleteInstructions(instructions: Instructions)

    @Query("SELECT * FROM instructions WHERE recipe_name = :recipeName ORDER BY instruction_id")
    fun getInstructionsForRecipe(recipeName: String): List<Instructions>

    @Query("SELECT MAX(instruction_id) FROM instructions WHERE recipe_name = :recipeName")
    fun getMaxInstructionIdForRecipe(recipeName: String): Int?
}


@Dao
interface IngredientsDao {

    @Insert
    fun createIngredient(ingredients: Ingredients)

    @Query("SELECT * FROM recipe_ingredients WHERE recipe_name = :recipeName")
    fun getIngredientsForRecipe(recipeName: String): List<Ingredients>

    @Update
    fun updateIngredients (ingredients: Ingredients)

    @Delete
    fun deleteIngredients (ingredients: Ingredients)
}
