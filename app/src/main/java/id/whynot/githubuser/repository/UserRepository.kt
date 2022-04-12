package id.whynot.githubuser.repository

import id.whynot.githubuser.api.API
import id.whynot.githubuser.callback.UserDetailCallback

import id.whynot.githubuser.response.UserDetailResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object UserRepository {
    private val USER_SERVICE: API =
        Retrofit.Builder()
            .baseUrl("https://api.github.com/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java)


    fun getUserDetail(username: String, callback: UserDetailCallback) {
        USER_SERVICE.getUserDetail(username).enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        callback.onSuccess(response.body() ?: UserDetailResponse())
                    } else {
                        callback.onFailure("response.body() is null")
                    }
                } else {
                    callback.onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                callback.onFailure(t.localizedMessage ?: "getUserDetail failure")
            }
        })
    }


}