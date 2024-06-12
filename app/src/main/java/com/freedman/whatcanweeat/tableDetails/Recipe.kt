package com.freedman.whatcanweeat.tableDetails


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freedman.whatcanweeat.R
import java.io.Serializable

@Entity (tableName = "recipe")
data class Recipe(
    @ColumnInfo(name = "recipe_name")@PrimaryKey val recipeName: String,
    val description: String? = null,
    val canWeMake: Boolean = false,
    val image: Int = R.drawable.default_image,
    val favourite: Boolean = false
):Serializable

