package com.devis.get_github_user.core.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devis.get_github_user.R
import com.devis.get_github_user.model.RepositoryMdl
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_user_repositories.view.*

/**
 * Created by devis on 23/06/20
 */

class RepositoryAdapter(private val repoList: List<RepositoryMdl>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    private var mIsShowLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder?
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                val view = layoutInflater.inflate(R.layout.item_user_repositories, parent, false)
                viewHolder = ItemViewHolder(view)
                viewHolder
            }
            ITEM_VIEW_TYPE_LOADING -> {
                val view = layoutInflater.inflate(R.layout.item_load_more, parent, false)
                viewHolder = LoadingViewHolder(view)
                viewHolder
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_user_repositories, parent, false)
                viewHolder = ItemViewHolder(view)
                viewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return repoList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= repoList.size) {
            ITEM_VIEW_TYPE_LOADING
        } else {
            ITEM_VIEW_TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder.itemViewType == ITEM_VIEW_TYPE_CONTENT) {
            val contentViewHolder: ItemViewHolder = holder as ItemViewHolder
            contentViewHolder.setIsRecyclable(false)
            contentViewHolder.bind(repoList[position])
        } else if (holder.itemViewType == ITEM_VIEW_TYPE_LOADING) {
            val loadingViewHolder: LoadingViewHolder = holder as LoadingViewHolder
            loadingViewHolder.showLoading(mIsShowLoading)
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(repo: RepositoryMdl) {
            with(itemView) {
                tv_repository_name.text = repo.name
                tv_repository_description.text = repo.description
                tv_repository_language.text = repo.language
                tv_repository_watchers_count.text = repo.watchersCount.toString()
                tv_repository_stargazers_count.text = repo.stargazersCount.toString()
                tv_repository_forks_count.text = repo.forksCount.toString()
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun showLoading(isShowLoading: Boolean) {
            if (isShowLoading) {
                itemView.load_more.visibility = View.VISIBLE
            } else {
                itemView.load_more.visibility = View.GONE
            }
        }
    }

    fun showLoading(isShow: Boolean) {
        mIsShowLoading = isShow
    }

}