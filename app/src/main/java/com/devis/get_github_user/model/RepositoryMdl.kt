package com.devis.get_github_user.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by devis on 23/06/20
 */

class RepositoryMdl : Serializable {
    @SerializedName("id")
    val id: Long = 0
    @SerializedName("name")
    val name: String = ""
    @SerializedName("description")
    val description: String? = null
    @SerializedName("stargazers_count")
    val stargazersCount = 0
    @SerializedName("watchers_count")
    val watchersCount = 0
    @SerializedName("forks_count")
    val forksCount = 0
    @SerializedName("language")
    val language: String? = null
}