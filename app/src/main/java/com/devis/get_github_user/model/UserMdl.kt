package com.devis.get_github_user.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by devis on 22/06/20
 */

class UserMdl : Serializable {
    @SerializedName("total_count")
    val totalCount: Int? = null
    @SerializedName("items")
    val items: List<UserDetailMdl>? = null

    @SerializedName("message")
    val message: String? = null
}

class UserDetailMdl : Serializable {
    @SerializedName("login")
    val login: String = ""
    @SerializedName("id")
    val id: Long = 0
    @SerializedName("avatar_url")
    val avatarUrl: String = ""

    @SerializedName("name")
    val name: String? = null
    @SerializedName("following")
    val following: Int? = null
    @SerializedName("followers")
    val followers: Int? = null
}

class UserStarredDetailMdl : Serializable {
    @SerializedName("id")
    val id: Long = 0
    @SerializedName("name")
    val name: String = ""
}