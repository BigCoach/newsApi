package com.example.newsapitest.util

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import org.apache.commons.io.FilenameUtils
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.random.Random

object FileUtils {

    fun saveImageToDownloadsFromUrl(
        context: Context,
        url: String
    ) {
        Timber.d("saveImageToGalleryFromUrl starts")
        val uri = url.toUri()
        // Create request for android download manager
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )

        val name = url.substring(url.lastIndexOf('/') + 1)

        // set title and description
        request.setTitle(name)
        request.setDescription("Загрузка $name")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        //set the local destination for download file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            name
        )
        request.setMimeType(getMimeType(context, uri) ?: "image/png")
        downloadManager.enqueue(request)

    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        Timber.d("getMimeType uri: $uri")
        var mimeType: String? = null
        try {
            val extension = FilenameUtils.getExtension(uri.toString())
            val lowerCaseUriString =
                uri.toString().replace(extension, extension.lowercase(Locale.getDefault()))
            mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                val cr = context.contentResolver
                cr.getType(lowerCaseUriString.toUri())
            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                    uri
                        .toString()
                )
                Timber.d("getMimeType fileExtension: $fileExtension")

                MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.lowercase(Locale.getDefault())
                )
            }
        } catch (e: Exception) {
            Timber.d("getMimeType error:$e ")
        }
        return mimeType
    }


}