package com.devis.get_github_user.utils


/**
 * Created by devis on 22/06/20
 */

sealed class ResultState<out T : Any?> {
    data class Success<out T : Any?>(
        val data: T?,
        val isLast: Boolean = false,
        val headers: okhttp3.Headers? = null) : ResultState<T>()
    data class Error(val errorMessage: String?) : ResultState<Nothing>()
    /*data class ErrorWithCode(
        val code: Int, val errorMessage: String?) : ResultState<Nothing>()*/
}