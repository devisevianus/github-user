package com.devis.get_github_user.core.detail

import com.devis.get_github_user.base.BasePresenter
import com.devis.get_github_user.repo.UserRepository
import com.devis.get_github_user.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by devis on 23/06/20
 */

class UserDetailPresenter(mView: UserDetailView) : BasePresenter<UserDetailView>(mView) {

    private val mRepository = UserRepository()

    fun getUserDetail(username: String) {
        mView.showLoading()
        mView.launch {
            withContext(Dispatchers.Main) {
                when (val response = mRepository.getUserDetail(username)) {
                    is ResultState.Success -> {
                        mView.stopLoading()
                        mView.onSuccessGetUserDetail(response.data)
                    }
                    is ResultState.Error -> {
                        mView.stopLoading()
                        mView.errorLoading(response.errorMessage.toString())
                    }
                }
            }
        }
    }

    fun getUserStarred(username: String) {
        mView.showLoading()
        mView.launch {
            withContext(Dispatchers.Main) {
                when (val response = mRepository.getUserStarred(username)) {
                    is ResultState.Success -> {
                        mView.stopLoading()
                        mView.onSuccessGetUserStarred(response.data, response.headers?.get("link"))
                    }
                    is ResultState.Error -> {
                        mView.stopLoading()
                        mView.errorLoading(response.errorMessage.toString())
                    }
                }
            }
        }
    }

    fun getUserRepositories(username: String, page: Int) {
        mView.launch {
            withContext(Dispatchers.Main) {
                when (val response = mRepository.getUserRepositories(username, page)) {
                    is ResultState.Success -> {
                        mView.onSuccessGetUserRepositories(response.data)
                    }
                    is ResultState.Error -> {
                        mView.errorLoading(response.errorMessage.toString())
                    }
                }
            }
        }
    }

}