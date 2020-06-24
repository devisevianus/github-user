package com.devis.get_github_user.base

/**
 * Created by devis on 22/06/20
 */

interface BaseView {
    fun showLoading()
    fun stopLoading()
    fun errorLoading(message: String)
}