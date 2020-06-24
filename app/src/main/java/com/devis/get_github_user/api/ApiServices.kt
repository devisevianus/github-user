package com.devis.get_github_user.api

import com.devis.get_github_user.model.RepositoryMdl
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserMdl
import com.devis.get_github_user.model.UserStarredDetailMdl
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by devis on 22/06/20
 */

interface ApiServices {

    @GET("search/users")
    suspend fun getUsers(
        @Query("q") keyword: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<UserMdl>

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Path("username") username: String
    ): Response<UserDetailMdl>

    @GET("users/{username}/starred")
    suspend fun getUserStarred(
        @Path("username") username: String,
        @Query("per_page") per_page: Int = 1
    ): Response<List<UserStarredDetailMdl>>

    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 5
    ): Response<List<RepositoryMdl>>

}