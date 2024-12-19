package tn.esprit.outfitaura2.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import tn.esprit.outfitaura2.models.Image

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: Image)

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<Image>
}
