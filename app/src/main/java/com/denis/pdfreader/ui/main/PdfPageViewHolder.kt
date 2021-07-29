package com.denis.pdfreader.ui.main

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denis.pdfreader.R
import com.denis.pdfreader.databinding.PdfPageHolderBinding

class PdfPageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup): PdfPageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pdf_page_holder, parent, false)
            return PdfPageViewHolder(view)
        }
    }

    fun bind(image: Bitmap) = with(itemView) {
        val binding = PdfPageHolderBinding.bind(itemView)
        binding.pdfPlaceholder.setImageBitmap(image)
    }
}