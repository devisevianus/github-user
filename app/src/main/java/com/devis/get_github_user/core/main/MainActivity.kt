package com.devis.get_github_user.core.main

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devis.get_github_user.R
import com.devis.get_github_user.base.BaseActivity
import com.devis.get_github_user.core.detail.UserDetailActivity
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserMdl
import com.devis.get_github_user.utils.EndlessRecyclerViewScrollListener
import com.devis.get_github_user.utils.loge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), MainView, MainAdapter.OnClickListener {

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var mPresenter: MainPresenter
    private lateinit var mAdapter: MainAdapter

    private var mUserList: ArrayList<UserDetailMdl> = arrayListOf()
    private var mLoadMoreListener: RecyclerView.OnScrollListener? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mKeyword: String = ""
    private var mPage: Int = 1
    private var mErrorMessage = ""
    private var mClickBackCounter = 0
    private var mLoadMore = false
    private var mIsRefreshData = false
    private var mIsInitial = true

    private val mTextWatcherListener : TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (et_search_user.hasFocus()) {
                mKeyword = s.toString()
                mPage = 1
                try {
                    mHandler?.removeCallbacks(mRunnable!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mRunnable = Runnable {
                    mUserList.clear()
                    if (s.isNullOrEmpty()) {
                        mAdapter.isLast = true
                        mAdapter.notifyDataSetChanged()
                    } else {
                        mPresenter.getUsers(mKeyword, mPage)
                    }
                }

                mHandler!!.postDelayed(mRunnable!!, 500)

                if (mKeyword.isNotEmpty()) {
                    et_search_user.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_search),
                        null,
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete),
                        null
                    )
                } else {
                    et_search_user.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_search),
                        null,
                        null,
                        null
                    )
                }
            }
        }

    }

    override val job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = MainPresenter(this)
        mHandler = Handler()

        initRecyclerView()
        getInitialData(savedInstanceState)
        initView()
        initViewAction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userList", Gson().toJson(mUserList))
        outState.putString("keyword", mKeyword)
        outState.putInt("page", mPage)
        outState.putString("errorMessage", mErrorMessage)
    }

    override fun onBackPressed() {
        mClickBackCounter++

        this.lifecycleScope.launch {
            if (mClickBackCounter > 1) {
                finishAffinity()
                mClickBackCounter = 0
            } else {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.message_exit_app),
                    Toast.LENGTH_SHORT).show()
            }
            delay(2000)
            mClickBackCounter = 0
        }

    }

    override fun layoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onSuccessGetUsers(data: UserMdl?) {
        mLoadMore = false
        mErrorMessage = ""
        mAdapter.showLoading(false)
        if (mIsRefreshData) {
            mUserList.clear()
            mIsRefreshData = false
        }
        rv_users.visibility = View.VISIBLE
        if (data?.items != null) {
            if (data.items.isNotEmpty()) {
                mUserList.addAll(data.items)
            }
        } else {
            Toast.makeText(this, data?.message, Toast.LENGTH_SHORT).show()
            data?.message?.loge(TAG)
        }
        mAdapter.isLast = mUserList.size == data?.totalCount
        mAdapter.notifyDataSetChanged()
        showOrHideRecyclerView()
    }

    override fun showLoading() {
        if (mPage == 1) {
            swiperefresh_main.isRefreshing = true
        }
    }

    override fun stopLoading() {
        swiperefresh_main.isRefreshing = false
    }

    override fun errorLoading(message: String) {
        mAdapter.showLoading(false)
        mAdapter.isLast = true
        mAdapter.notifyDataSetChanged()
        mLoadMore = true
        if (message.contains("limit", ignoreCase = true)) {
            Toast.makeText(this, resources.getString(R.string.message_limit_exceed), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        mErrorMessage = message
        message.loge(TAG)
    }

    override fun onItemClickListener(user: UserDetailMdl) {
        UserDetailActivity.startThisActivity(this, user.login)
    }

    private fun getInitialData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mKeyword = savedInstanceState.getString("keyword").toString()
            mPage = savedInstanceState.getInt("page")
            mErrorMessage = savedInstanceState.getString("errorMessage").toString()
            mUserList.addAll(Gson().fromJson(
                savedInstanceState.getString("userList"),
                object : TypeToken<ArrayList<UserDetailMdl>>() {}.type))
            mAdapter.notifyDataSetChanged()
            showOrHideRecyclerView()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mUserList.clear()
        mAdapter = MainAdapter(mUserList, this)
        mLoadMoreListener =
            object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    if (mKeyword.isNotEmpty() && !mIsInitial) {
                        mPage++
                        loadData()
                        mLoadMore = false
                    }
                }

                override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(view, dx, dy)
                    if (!rv_users.canScrollVertically(1)) {
                        if (mErrorMessage.isNotBlank() || mErrorMessage.isNotEmpty() && mLoadMore) {
                            loadData()
                            mLoadMore = false
                        }
                    }
                }
            }
        rv_users.apply {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            addOnScrollListener(mLoadMoreListener as EndlessRecyclerViewScrollListener)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun initView() {
        toolbar_main.requestFocus()
        et_search_user.clearFocus()
        et_search_user.isCursorVisible = false
        swiperefresh_main.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))

        et_search_user.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                et_search_user.isCursorVisible = true
                et_search_user.hint = resources.getString(R.string.hint_search_users)
                if (!et_search_user.text.isNullOrEmpty()) {
                    et_search_user.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_search),
                        null,
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete),
                        null
                    )
                }
            } else {
                et_search_user.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_search),
                    null,
                    null,
                    null
                )
            }
        }
    }

    private fun initViewAction() {
        swiperefresh_main.setOnRefreshListener {
            (mLoadMoreListener as EndlessRecyclerViewScrollListener).resetState()
            mIsRefreshData = true
            mPage = 1
            mPresenter.getUsers(mKeyword, mPage)
        }

        et_search_user.setOnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (et_search_user.compoundDrawables[drawableRight] != null) {
                    if (event.x >= (et_search_user.width - et_search_user.totalPaddingRight)) {
                        et_search_user.setText("")
                    }
                }
            }

            false
        }

        et_search_user.addTextChangedListener(mTextWatcherListener)
    }

    private fun showOrHideRecyclerView() {
        if (mUserList.isEmpty()) {
            rv_users.visibility = View.GONE
            layout_no_users.visibility = View.VISIBLE
        } else {
            rv_users.visibility = View.VISIBLE
            layout_no_users.visibility = View.GONE
        }
    }

    private fun loadData() {
        if (mPage != 1) {
            rv_users.post {
                mAdapter.showLoading(true)
                mAdapter.notifyDataSetChanged()
            }
        }

        mPresenter.getUsers(mKeyword, mPage)
    }

}
