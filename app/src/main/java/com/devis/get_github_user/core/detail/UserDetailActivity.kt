package com.devis.get_github_user.core.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devis.get_github_user.R
import com.devis.get_github_user.base.BaseActivity
import com.devis.get_github_user.model.RepositoryMdl
import com.devis.get_github_user.model.UserDetailMdl
import com.devis.get_github_user.model.UserStarredDetailMdl
import com.devis.get_github_user.utils.EndlessRecyclerViewScrollListener
import com.devis.get_github_user.utils.loge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.layout_header_user_detail.*
import kotlinx.android.synthetic.main.toolbar_primary.*
import kotlinx.android.synthetic.main.toolbar_primary.view.*
import kotlinx.coroutines.Job

/**
 * Created by devis on 23/06/20
 */

class UserDetailActivity : BaseActivity(), UserDetailView {

    companion object {
        private val TAG: String = UserDetailActivity::class.java.simpleName

        fun startThisActivity(context: Context, userId: String) {
            val intent = Intent(context, UserDetailActivity::class.java)
            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }
    }

    private lateinit var mPresenter: UserDetailPresenter
    private lateinit var mAdapter: RepositoryAdapter

    private var mUserId: String? = null
    private var mRepoList: ArrayList<RepositoryMdl> = arrayListOf()
    private var mUserDetail: UserDetailMdl? = null
    private var mUserStarredList: ArrayList<UserStarredDetailMdl> = arrayListOf()
    private var mLinkHeader: String? = null
    private var mLoadMoreListener: RecyclerView.OnScrollListener? = null
    private var mPage = 1

    override val job = Job()

    override fun layoutRes(): Int {
        return R.layout.activity_user_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = UserDetailPresenter(this)

        initToolbar()
        initRecyclerView()
        getInitialData(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userId", mUserId)
        outState.putString("repoList", Gson().toJson(mRepoList))
        outState.putString("starredList", Gson().toJson(mUserStarredList))
        outState.putString("linkHeader", mLinkHeader)
        outState.putSerializable("userDetail", mUserDetail)
    }

    override fun onSuccessGetUserDetail(data: UserDetailMdl?) {
        mUserDetail = data
        mPresenter.getUserStarred(data?.login.toString())
    }

    override fun onSuccessGetUserStarred(data: List<UserStarredDetailMdl>?, link: String?) {
        mUserStarredList.clear()
        if (data != null) {
            mUserStarredList.addAll(data)
        }
        mLinkHeader = link
        initView()
        mPresenter.getUserRepositories(mUserId.toString(), mPage)
    }

    override fun onSuccessGetUserRepositories(data: List<RepositoryMdl>?) {
        mAdapter.showLoading(false)
        if (!data.isNullOrEmpty()) {
            mRepoList.addAll(data)
        }
        mAdapter.notifyDataSetChanged()
        showOrHideRecyclerView()
    }

    override fun showLoading() {
        startProgress()
    }

    override fun stopLoading() {
        stopProgress()
    }

    override fun errorLoading(message: String) {
        mAdapter.showLoading(false)
        mAdapter.notifyDataSetChanged()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        message.loge(TAG)
    }

    private fun getInitialData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mUserId = savedInstanceState.getString("userId")
            mLinkHeader = savedInstanceState.getString("linkHeader")
            mRepoList.addAll(Gson().fromJson(
                savedInstanceState.getString("repoList"),
                object : TypeToken<ArrayList<RepositoryMdl>>() {}.type))
            mUserStarredList = Gson().fromJson(
                savedInstanceState.getString("starredList"),
                object : TypeToken<ArrayList<UserStarredDetailMdl>>() {}.type)
            mUserDetail = savedInstanceState.getSerializable("userDetail") as UserDetailMdl
            mAdapter.notifyDataSetChanged()
            initView()
            showOrHideRecyclerView()
        } else {
            mUserId = intent.getStringExtra("userId")
            mPresenter.getUserDetail(mUserId.toString())
        }
    }

    private fun initToolbar() {
        toolbar.tv_toolbar_title.text = resources.getString(R.string.title_user_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_chevron_left))
    }

    private fun initView() {
        Glide.with(this)
            .load(mUserDetail?.avatarUrl)
            .into(iv_user_avatar)

        tv_user_name.text = mUserDetail?.name
        tv_user_username.text = mUserDetail?.login
        tv_total_followers.text = mUserDetail?.followers.toString()
        tv_total_following.text = mUserDetail?.following.toString()

        if (mUserStarredList.isNullOrEmpty()) {
            tv_total_starred.text = "0"
        } else {
            if (mLinkHeader != null) {
                val arrayLink = mLinkHeader?.split(",")
                val substring1 = arrayLink!![1].substringAfter("&")
                val substring2 = substring1.substringBefore(">")
                val page = substring2.substringAfter("=")
                tv_total_starred.text = page
            } else {
                tv_total_starred.text = mUserStarredList.size.toString()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mAdapter = RepositoryAdapter(mRepoList)
        mLoadMoreListener =
            object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    mPage++
                    loadData()
                }
            }

        rv_user_repositories.apply {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            addOnScrollListener(mLoadMoreListener as EndlessRecyclerViewScrollListener)
        }
    }

    private fun showOrHideRecyclerView() {
        if (mRepoList.isEmpty()) {
            iv_no_repository.visibility = View.VISIBLE
            layout_repository.visibility = View.GONE
        } else {
            iv_no_repository.visibility = View.GONE
            layout_repository.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        if (mPage != 1) {
            rv_user_repositories.post {
                mAdapter.showLoading(true)
                mAdapter.notifyDataSetChanged()
            }
        }

        mPresenter.getUserRepositories(mUserId.toString(), mPage)
    }

}