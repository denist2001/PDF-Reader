package com.denis.pdfreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.denis.pdfreader.ui.main.BitmapFlowPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class PageProviderViewModel : ViewModel() {

    private lateinit var renderer: PdfRenderer
    private lateinit var pagingSource: BitmapFlowPagingSource

    private val _bitmapsFlow = MutableSharedFlow<PagingData<Bitmap>>()
    val bitmapsFlow: Flow<PagingData<Bitmap>>
        get() = _bitmapsFlow

    suspend fun loadFirstPage(
        context: Context,
        filePath: String
    ) {
        return withContext(Dispatchers.IO) {
            val fileUri = filePath.toUri()
            val fileDescriptor = context.contentResolver.openFileDescriptor(
                fileUri, "r"
            )
            fileDescriptor ?: return@withContext

            renderer = PdfRenderer(fileDescriptor)
            pagingSource = BitmapFlowPagingSource(renderer)
            Pager(
                config = PagingConfig(
                    pageSize = 3,
                    enablePlaceholders = true,
                    prefetchDistance = 2,
                    initialLoadSize = 5
                ),
                pagingSourceFactory = { pagingSource }
            ).flow.collectLatest {
                _bitmapsFlow.emit(it)
            }
        }
    }

    fun close() {
        renderer.close()
    }
}