package com.example.accelerationestimator.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.adriankuta.expandable_recyclerview.ExpandableRecyclerViewAdapter
import com.github.adriankuta.expandable_recyclerview.ExpandableTreeNode


class ExpandableAdapter: ExpandableRecyclerViewAdapter<String, RecyclerView.ViewHolder>() {


    override fun getTreeNodes(): ExpandableTreeNode<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        treeNode: ExpandableTreeNode<String>,
        nestLevel: Int
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup, nestLevel: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}