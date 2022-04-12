package id.whynot.githubuser.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.whynot.githubuser.callback.UserDetailCallback
import id.whynot.githubuser.db.FavoriteUser
import id.whynot.githubuser.db.FavoriteUserRepository
import id.whynot.githubuser.repository.UserRepository
import id.whynot.githubuser.response.UserDetailResponse


class UserDetailViewModel(application: Application, username: String) :
    ViewModel() {


    private val favoriteUserRepository = FavoriteUserRepository(application)

    private val _user = MutableLiveData<UserDetailResponse>()
    val user: LiveData<UserDetailResponse> = _user

    private val _isLoading = MutableLiveData<Boolean>()


    private val _isFailure = MutableLiveData<Boolean>()


    init {
        fetchUserDetail(username)
    }

    private fun fetchUserDetail(username: String) {
        // show progressbar
        _isLoading.value = true

        UserRepository.getUserDetail(username, object : UserDetailCallback {
            override fun onSuccess(userDetail: UserDetailResponse) {
                _isLoading.value = false // hide progressbar
                _isFailure.value = false  // success fetch data
                _user.value = userDetail
            }

            override fun onFailure(message: String) {
                // unable to fetch data
                _isFailure.value = true
                Log.d(TAG, message)
            }
        })
    }

    fun insert(favoriteUser: FavoriteUser) {
        favoriteUserRepository.insert(favoriteUser)
    }

    fun delete(favoriteUser: FavoriteUser) {
        favoriteUserRepository.delete(favoriteUser)
    }

    fun getFavoriteUser(username: String): FavoriteUser {
        return favoriteUserRepository.getFavoriteUser(username)
    }

    fun isFavoriteUserExists(username: String): Boolean {
        return favoriteUserRepository.isFavoriteUserExists(username)
    }

    companion object {
        private const val TAG = "UserDetailViewModel"
    }
}