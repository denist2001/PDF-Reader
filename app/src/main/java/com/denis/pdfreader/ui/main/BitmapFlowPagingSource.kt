package com.denis.pdfreader.ui.main

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.paging.PagingSource
import androidx.paging.PagingState

class BitmapFlowPagingSource(private val renderer: PdfRenderer) : PagingSource<Int, Bitmap>() {
    private val bitmaps = ArrayList<Bitmap>()

    init {
        for (key in 0 until renderer.pageCount) {
            val bitmap = Bitmap.createBitmap(768, 1024, Bitmap.Config.ARGB_8888)
            val page = renderer.openPage(key)//0 -> current page -> renderer.pageCount
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            val renderedBitmap = bitmap ?: continue
            bitmaps.add(renderedBitmap)
            page.close()
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bitmap> {
        return try {//params.key
            val key = params.key ?: return LoadResult.Error(Throwable("PDF reader error"))
            LoadResult.Page(
                data = bitmaps,
                prevKey = null,
                nextKey = renderer.pageCount
            )
        } catch (exception: Exception) {
            return LoadResult.Error(Throwable("Network error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Bitmap>): Int {
        return state.anchorPosition ?: 0
    }
}
