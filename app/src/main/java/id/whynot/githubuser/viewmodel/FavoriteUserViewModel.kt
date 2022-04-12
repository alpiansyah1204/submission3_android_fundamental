package id.whynot.githubuser.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.whynot.githubuser.db.FavoriteUser
import id.whynot.githubuser.db.FavoriteUserRepository


class FavoriteUserViewModel(application: Application) : ViewModel() {
    private val favoriteUserRepository = FavoriteUserRepository(application)

    fun getAllFavoriteUsers(): LiveData<List<FavoriteUser>> {
        return favoriteUserRepository.getAllFavoriteUsers()
    }

    fun deleteAllFavorites() {
        favoriteUserRepository.deleteAllFavoriteUsers()
    }
}