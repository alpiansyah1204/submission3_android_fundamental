package id.whynot.githubuser.callback

import id.whynot.githubuser.response.UserDetailResponse

interface UserDetailCallback {
    fun onSuccess(userDetail: UserDetailResponse)
    fun onFailure(message: String)
}