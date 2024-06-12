package com.freedman.whatcanweeat.tableDetails


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freedman.whatcanweeat.R
import java.io.Serializable

@Entity (tableName = "recipe_ingredients", primaryKeys = ["recipe_name", "ingredient" ])
data class Ingredients(
    @ColumnInfo(name = "recipe_name") val recipeName: String,
    val ingredient: String,
    val optional: Boolean?
)

