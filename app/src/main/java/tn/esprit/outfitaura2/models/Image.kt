package tn.esprit.outfitaura2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String, // Or file path
    val label: String
)
