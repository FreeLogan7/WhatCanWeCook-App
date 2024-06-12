package com.freedman.whatcanweeat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.freedman.whatcanweeat.tableDetails.Groceries
import com.freedman.whatcanweeat.tableDetails.Ingredients
import com.freedman.whatcanweeat.tableDetails.Instructions
import com.freedman.whatcanweeat.tableDetails.Recipe


@Database(entities = [Recipe::class, Groceries::class, Instructions::class, Ingredients::class], version = 6)
abstract class WhatCanWeEatDatabase : RoomDatabase() {

    abstract fun getRecipeDao(): RecipeDao
    abstract fun getGroceryDao(): GroceryDao
    abstract fun getInstructionsDao(): InstructionsDao
    abstract fun getIngredientsDao(): IngredientsDao

    companion object {
        @Volatile
        private var DATABASE_INSTANCE: WhatCanWeEatDatabase? = null

        fun getDatabase(context: Context): WhatCanWeEatDatabase {
            return DATABASE_INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context,
                    WhatCanWeEatDatabase::class.java,
                    "what-can-we-eat-database"
                )
//                    .addMigrations(MIGRATION_1_2)
//                    .addMigrations(MIGRATION_2_3)
//                    .addMigrations(MIGRATION_3_4)
//                    .addMigrations((MIGRATION_4_5))
//                    .addMigrations(MIGRATION_5_6)
                    .build()
                DATABASE_INSTANCE = instance
                return instance
            }
        }


//        private val MIGRATION_5_6 = object : Migration(5, 6) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL(
//                    """
//                    CREATE TABLE IF NOT EXISTS `recipe_ingredients`
//                    (`recipe_name` TEXT NOT NULL,
//                    `ingredient` TEXT NOT NULL,
//                    'optional' INTEGER,
//                    PRIMARY KEY('recipe_name' , 'ingredient' ))
//                """.trimIndent())
//            }
//
//        }




//        private val MIGRATION_4_5 = object : Migration(4, 5) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL(
//                    """
//                    CREATE TABLE IF NOT EXISTS `instructions`
//                    (`recipe_name` TEXT NOT NULL,
//                    `instruction_id` INTEGER NOT NULL,
//                    'instructions' TEXT NOT NULL,
//                    'image' INTEGER,
//                    PRIMARY KEY('recipe_name' , 'instruction_id' ))
//                """.trimIndent())
//            }
//
//        }


//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                // Create the new table
//                db.execSQL(
//                    """
//            CREATE TABLE recipe_new (
//                recipe_name TEXT NOT NULL PRIMARY KEY,
//                description TEXT,
//                canWeMake INTEGER NOT NULL DEFAULT 0,
//                image INTEGER NOT NULL DEFAULT ${R.drawable.default_image},
//                favourite INTEGER NOT NULL DEFAULT 0
//            )
//        """.trimIndent()
//                )
//
//                // Copy the data
//                db.execSQL(
//                    """
//            INSERT INTO recipe_new (recipe_name, description, canWeMake)
//            SELECT name, description, canWeMake FROM recipe
//        """.trimIndent()
//                )
//
//                // Drop the old table
//                db.execSQL("DROP TABLE recipe")
//
//                // Rename the new table to the old table's name
//                db.execSQL("ALTER TABLE recipe_new RENAME TO recipe")
//            }
//        }
//
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS `groceries` (`grocery_name` TEXT PRIMARY KEY NOT NULL, `inFridge` INTEGER NOT NULL DEFAULT 0, 'image' INTEGER NOT NULL)"
//                )
//            }
//        }

//        val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE `groceries_new` (`grocery_name` TEXT PRIMARY KEY NOT NULL, `inFridge` INTEGER NOT NULL DEFAULT 0, 'image' INTEGER)"
//                )
//
//                database.execSQL("""
//                    INSERT INTO groceries_new (grocery_name, inFridge, image)
//                    SELECT grocery_name, inFridge, NULL
//                    FROM groceries
//                """.trimIndent())
//
//                database.execSQL("DROP TABLE groceries")
//
//                database.execSQL("ALTER TABLE groceries_new RENAME TO groceries")
//
//
//            }
//        }


    }
}


// .fallbackToDestructiveMigration()
