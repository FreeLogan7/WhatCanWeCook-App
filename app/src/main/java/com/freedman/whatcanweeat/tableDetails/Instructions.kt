package com.freedman.whatcanweeat.tableDetails


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freedman.whatcanweeat.R
import java.io.Serializable

@Entity (tableName = "instructions", primaryKeys = ["recipe_name", "instruction_id" ])
data class Instructions(
    @ColumnInfo(name = "recipe_name") val recipeName: String,
    @ColumnInfo(name = "instruction_id") val id: Int,
    val instructions: String,
    val image: Int?
)

