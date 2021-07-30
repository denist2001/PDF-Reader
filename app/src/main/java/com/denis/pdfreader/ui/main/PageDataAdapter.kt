package com.denis.pdfreader.ui.main

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class PageDataAdapter : PagingDataAdapter<Bitmap, PdfPageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        return PdfPageViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

class DiffCallback : DiffUtil.ItemCallback<Bitmap>() {
    override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
        return oldItem.sameAs(newItem)
    }

    override fun areContentsTheSame(
        oldItem: Bitmap,
        newItem: Bitmap
    ): Boolean {
        return oldItem.sameAs(newItem)
    }
}