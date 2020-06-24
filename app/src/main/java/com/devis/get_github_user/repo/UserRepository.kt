package com.devis.get_github_user.repo

import com.devis.get_github_user.BuildConfig
import com.devis.get_github_user.api.ApiClient
import com.devis.get_github_user.model.RepositoryMdl
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserMdl
import com.devis.get_github_user.model.UserStarredDetailMdl
import com.devis.get_github_user.utils.ResultState
import com.devis.get_github_user.utils.fetchState

/**
 * Created by devis on 22/06/20
 */

class UserRepository : UserRequest {

    private val mClient = ApiClient(BuildConfig.BASE_URL)

    override suspend fun getUsers(keyword: String, page: Int): ResultState<UserMdl> {
        return fetchState {
            val response = mClient.getApiServices().getUsers(keyword, page, per_page = 10)
            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                ResultState.Error(response.message())
            }
        }
    }

    override suspend fun getUserDetail(username: String): ResultState<UserDetailMdl> {
        return fetchState {
            val response = mClient.getApiServices().getUserDetail(username)
            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                ResultState.Error(response.message())
            }
        }
    }

    override suspend fun getUserStarred(username: String): ResultState<List<UserStarredDetailMdl>> {
        return fetchState {
            val response = mClient.getApiServices().getUserStarred(username)
            if (response.isSuccessful) {
                ResultState.Success(response.body(), headers = response.headers())
            } else {
                ResultState.Error(response.message())
            }
        }
    }

    override suspend fun getUserRepositories(
        username: String,
        page: Int
    ): ResultState<List<RepositoryMdl>> {
        return fetchState {
            val response = mClient.getApiServices().getUserRepositories(username, page)
            if (response.isSuccessful) {
                ResultState.Success(response.body())
            } else {
                ResultState.Error(response.message())
            }
        }
    }

}