package com.denis.pdfreader.ui.main

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.paging.PagingSource
import androidx.paging.PagingState

class BitmapFlowPagingSource(private val renderer: PdfRenderer) : PagingSource<Int, Bitmap>() {
    private var previousPage: PdfRenderer.Page? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Bitmap> {
        try {//params.key
            val key = params.key ?: 0
            val prevKey = if (key - 1 < 0) null else {
                key - 1
            }
            val nextKey = if (key + 1 >= renderer.pageCount) null else {
                key + 1
            }
            //previous page has to be closed before we get new one
            previousPage?.close()
            val page = renderer.openPage(key)//0 -> current page -> renderer.pageCount
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            previousPage = page
            return LoadResult.Page(
                data = listOf(bitmap),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            return LoadResult.Error(Throwable("Reading of values error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Bitmap>): Int {
        return state.anchorPosition ?: 0
    }
}
