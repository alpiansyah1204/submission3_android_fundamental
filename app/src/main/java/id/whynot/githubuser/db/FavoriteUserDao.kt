package id.whynot.githubuser.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteUser: FavoriteUser)

    @Delete
    suspend fun delete(favoriteUser: FavoriteUser)

    @Query("DELETE FROM favorite_user")
    suspend fun deleteAllFavoriteUsers()

    @Query("SELECT EXISTS (SELECT * FROM favorite_user WHERE username = :username)")
    suspend fun isFavoriteUserExists(username: String): Boolean

    @Query("SELECT * FROM favorite_user WHERE username = :username LIMIT 1")
    suspend fun getFavoriteUser(username: String): FavoriteUser

    @Query("SELECT * FROM favorite_user ORDER BY id ASC")
    fun getAllFavoriteUsers(): LiveData<List<FavoriteUser>>
}