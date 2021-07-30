package com.denis.pdfreader.ui.main

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.paging.PagingSource
import androidx.paging.PagingState

class BitmapFlowPagingSource : PagingSource<Int, Bitmap>() {
    private var previousPage: PdfRenderer.Page? = null
    private lateinit var renderer: PdfRenderer
    fun setRenderer(renderer: PdfRenderer) {
        this.renderer = renderer
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bitmap> {
        try {//params.key
            val key = params.key ?: 0
            //previous page has to be closed before we get new one
            previousPage?.close()
            if (!this::renderer.isInitialized) return LoadResult.Error(Throwable("Pdf renderer is not initialised"))
            val page = renderer.openPage(key)//0 -> current page -> renderer.pageCount
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            page.close()
            previousPage = page
            return LoadResult.Page(
                data = listOf(bitmap),
                prevKey = null,
                nextKey = key + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(Throwable("Reading of values error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Bitmap>): Int {
        return state.anchorPosition ?: 0
    }
}
