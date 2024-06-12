package com.freedman.whatcanweeat.tableDetails


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "groceries")
data class Groceries(
    @ColumnInfo(name = "grocery_name")@PrimaryKey val groceryName: String,
    val inFridge: Boolean = false,
    val image: Int? = null
)

/* Grab a recipe
Grab the ingredients




 */