package com.devis.get_github_user.core.main

import com.devis.get_github_user.base.BasePresenter
import com.devis.get_github_user.repo.UserRepository
import com.devis.get_github_user.utils.ResultState
import kotlinx.coroutines.*

/**
 * Created by devis on 22/06/20
 */

class MainPresenter(mView: MainView) : BasePresenter<MainView>(mView) {

    private val mRepository = UserRepository()

    fun getUsers(keyword: String, page: Int) {
        mView.showLoading()
        mView.launch {
            withContext(Dispatchers.Main) {
                when (val response = mRepository.getUsers(keyword, page)) {
                    is ResultState.Success -> {
                        mView.stopLoading()
                        mView.onSuccessGetUsers(response.data)
                    }
                    is ResultState.Error -> {
                        mView.stopLoading()
                        mView.errorLoading(response.errorMessage.toString())
                    }
                }
            }
        }
    }

}