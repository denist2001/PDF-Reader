package com.denis.pdfreader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.denis.pdfreader.ui.main.PdfPagesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == OPEN_DOCUMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { documentUri ->

                /**
                 * Upon getting a document uri returned, we can use
                 * [ContentResolver.takePersistableUriPermission] in order to persist the
                 * permission across restarts.
                 *
                 * This may not be necessary for your app. If the permission is not
                 * persisted, access to the uri is granted until the receiving Activity is
                 * finished. You can extend the lifetime of the permission grant by passing
                 * it along to another Android component. This is done by including the uri
                 * in the data field or the ClipData object of the Intent used to launch that
                 * component. Additionally, you need to add FLAG_GRANT_READ_URI_PERMISSION
                 * and/or FLAG_GRANT_WRITE_URI_PERMISSION to the Intent.
                 *
                 * This app takes the persistable URI permission grant to demonstrate how, and
                 * to allow us to reopen the last opened document when the app starts.
                 */
                contentResolver.takePersistableUriPermission(
                    documentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                openDocument(documentUri)
            }
        }
    }

    private fun openDocument(documentUri: Uri) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PdfPagesFragment.newInstance(documentUri))
            .commitNow()
    }
}

private const val OPEN_DOCUMENT_REQUEST_CODE = 0x33