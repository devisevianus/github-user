package com.devis.get_github_user.core.detail

import com.devis.get_github_user.base.BaseView
import com.devis.get_github_user.model.RepositoryMdl
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserStarredDetailMdl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by devis on 23/06/20
 */

interface UserDetailView : BaseView, CoroutineScope {

    val job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun onSuccessGetUserDetail(data: UserDetailMdl?)
    fun onSuccessGetUserStarred(data: List<UserStarredDetailMdl>?, link: String?)
    fun onSuccessGetUserRepositories(data: List<RepositoryMdl>?)

}