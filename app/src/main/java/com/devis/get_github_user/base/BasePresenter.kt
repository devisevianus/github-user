package com.devis.get_github_user.base

import com.devis.get_github_user.api.ApiClient

/**
 * Created by devis on 22/06/20
 */

open class BasePresenter<V>(protected var mView: V) {
    protected var mClient: ApiClient? = null
}