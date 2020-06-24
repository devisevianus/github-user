package com.devis.get_github_user.base

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.devis.get_github_user.utils.ProgressDialog

/**
 * Created by devis on 22/06/20
 */

abstract class BaseActivity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null

    @LayoutRes
    protected abstract fun layoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun startProgress() {
        try {
            mProgressDialog = ProgressDialog()
            mProgressDialog!!.show(supportFragmentManager, "loading")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    fun stopProgress() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog!!.cancel()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

}