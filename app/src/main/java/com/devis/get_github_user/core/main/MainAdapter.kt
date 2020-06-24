package com.devis.get_github_user.core.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devis.get_github_user.R
import com.devis.get_github_user.model.UserDetailMdl
import kotlinx.android.synthetic.main.item_user.view.*

/**
 * Created by devis on 22/06/20
 */

class MainAdapter(
    private val userList: List<UserDetailMdl>,
    private val listener: OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    private var mIsShowLoading = false
    var isLast = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder?
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                val view = layoutInflater.inflate(R.layout.item_user, parent, false)
                viewHolder = ItemViewHolder(view)
                viewHolder
            }
            ITEM_VIEW_TYPE_LOADING -> {
                val view = layoutInflater.inflate(R.layout.item_load_more, parent, false)
                viewHolder = LoadingViewHolder(view)
                viewHolder
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_user, parent, false)
                viewHolder = ItemViewHolder(view)
                viewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return if (userList.size != 1) {
            when (isLast) {
                true -> { userList.size }
                false -> { userList.size + 1 }
            }
        } else {
            userList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= userList.size) {
            ITEM_VIEW_TYPE_LOADING
        } else {
            ITEM_VIEW_TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_VIEW_TYPE_CONTENT) {
            val contentViewHolder: ItemViewHolder = holder as ItemViewHolder
            contentViewHolder.setIsRecyclable(false)
            contentViewHolder.bind(userList[position])
        } else if (holder.itemViewType == ITEM_VIEW_TYPE_LOADING) {
            val loadingViewHolder: LoadingViewHolder = holder as LoadingViewHolder
            loadingViewHolder.showLoading(mIsShowLoading)
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: UserDetailMdl) {
            with(itemView) {
                Glide.with(context)
                    .load(user.avatarUrl)
                    .into(iv_user_avatar)

                tv_user_name.text = user.login

                setOnClickListener {
                    listener.onItemClickListener(user)
                }
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun showLoading(isShowLoading: Boolean) {
            if (isShowLoading) {
                itemView.visibility = View.VISIBLE
            } else {
                itemView.visibility = View.GONE
            }
        }
    }

    interface OnClickListener {

        fun onItemClickListener(user: UserDetailMdl)
    }

    fun showLoading(isShow: Boolean) {
        mIsShowLoading = isShow
    }

}