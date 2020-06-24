package com.devis.get_github_user.core.main

import com.devis.get_github_user.base.BaseView
import com.devis.get_github_user.model.UserMdl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by devis on 22/06/20
 */

interface MainView : BaseView, CoroutineScope {

    val job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun onSuccessGetUsers(data: UserMdl?)

}