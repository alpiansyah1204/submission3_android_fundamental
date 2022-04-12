package id.whynot.githubuser.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class FavoriteUserRepository(application: Application) {
    private val favoriteUserDao: FavoriteUserDao =
        FavoriteUserDatabase.getDatabase(application).getFavoriteUserDao()

    fun getAllFavoriteUsers(): LiveData<List<FavoriteUser>> {
        return favoriteUserDao.getAllFavoriteUsers()
    }

    fun getFavoriteUser(username: String): FavoriteUser {
        lateinit var favUser: FavoriteUser
        runBlocking {
            val temp = async {
                favoriteUserDao.getFavoriteUser(username)
            }
            favUser = temp.await()
        }
        return favUser
    }

    fun insert(favoriteUser: FavoriteUser) {
        runBlocking {
            favoriteUserDao.insert(favoriteUser)
        }
    }

    fun delete(favoriteUser: FavoriteUser) {
        runBlocking {
            favoriteUserDao.delete(favoriteUser)
        }
    }

    fun deleteAllFavoriteUsers() {
        runBlocking {
            favoriteUserDao.deleteAllFavoriteUsers()
        }
    }

    fun isFavoriteUserExists(username: String): Boolean {
        var isExists: Boolean
        runBlocking {
            val temp = async {
                favoriteUserDao.isFavoriteUserExists(username)
            }
            isExists = temp.await()
        }
        return isExists
    }
}