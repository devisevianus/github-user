package com.devis.get_github_user.repo

import com.devis.get_github_user.model.RepositoryMdl
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserMdl
import com.devis.get_github_user.model.UserStarredDetailMdl
import com.devis.get_github_user.utils.ResultState

/**
 * Created by devis on 22/06/20
 */

interface UserRequest {

    suspend fun getUsers(keyword: String, page: Int): ResultState<UserMdl>

    suspend fun getUserDetail(username: String): ResultState<UserDetailMdl>

    suspend fun getUserStarred(username: String): ResultState<List<UserStarredDetailMdl>>

    suspend fun getUserRepositories(username: String, page: Int): ResultState<List<RepositoryMdl>>

}