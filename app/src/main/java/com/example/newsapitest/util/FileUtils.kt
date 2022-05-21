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

    fun saveFileToPictures(context: Context, url: String) {
        val uri = url.toUri()
        saveFileToExternalPublicDirectory(
            context,
            url.substring(url.lastIndexOf('/') + 1),
            uri,
            Environment.DIRECTORY_PICTURES
        )
    }


    private fun saveFileToExternalPublicDirectory(
        context: Context,
        name: String,
        uri: Uri,
        publicDirectory: String
    ) {
        try {
            Timber.d("saveFileToExternalPublicDirectory. uri: $uri, publicDirectory: $publicDirectory")
            Timber.d("saveFileToExternalPublicDirectory starts")
            val downloadsDirectory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                context.getExternalFilesDir(publicDirectory) ?: context.cacheDir
            else Environment.getExternalStoragePublicDirectory(publicDirectory + File.separator + "KWORK")
            Timber.d("saveFileToExternalPublicDirectory downloadsDirectory: $downloadsDirectory")
            if (!downloadsDirectory.isDirectory) {
                Timber.d("saveFileToExternalPublicDirectory downloadsDirectory mkdirs")
                downloadsDirectory.mkdirs();
            }
            var finalName = name
            var destFile = File(downloadsDirectory, finalName)

            if (destFile.exists()) {
                val idx = finalName.lastIndexOf('.')
                for (a in 0..99) {
                    var copyName = finalName
                    copyName = if (idx != -1) {
                        copyName.substring(0, idx) + "(" + (a + 1) + ")" + copyName.substring(idx)
                    } else {
                        copyName + "(" + (a + 1) + ")"
                    }

                    destFile = File(downloadsDirectory, copyName)
                    if (!destFile.exists()) {
                        finalName = copyName
                        break
                    }
                }
            }
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            try {
                saveFileFromUri(
                    context,
                    uri,
                    destFile.absolutePath
                )
            } catch (e: SecurityException) {
                return
            }

            val mime = getMimeType(context, uri) ?: "file"
            // Determine where to save your file
            Timber.d("--- filePath: ${destFile.absolutePath} mime: $mime")

            try {
                manager.addCompletedDownload(
                    destFile.name, destFile.name,
                    true,
                    mime,
                    destFile.absolutePath,
                    destFile.length(),
                    true
                )
            } catch (e: java.lang.SecurityException) {
                e.printStackTrace()
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Timber.tag("+++Q+++")
                    .d("MediaStore.Downloads.SIZE: ${File(downloadsDirectory, finalName).length()}")
                Timber.tag("+++Q+++")
                    .d("MediaStore.Downloads.RELATIVE_PATH: ${Environment.DIRECTORY_DOWNLOADS}")

                val contentValues = ContentValues()
                contentValues.apply {
                    put(MediaStore.Downloads.BUCKET_ID, Random.nextLong())
                    put(MediaStore.Downloads.TITLE, finalName)
                    put(MediaStore.Downloads.DISPLAY_NAME, finalName)
                    put(MediaStore.Downloads.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis() / 1000)
                    put(MediaStore.Downloads.DATE_MODIFIED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Downloads.MIME_TYPE, mime)
                    put(MediaStore.Downloads.SIZE, File(downloadsDirectory, finalName).length())
                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                // Insert into the database
                val contentResolver = context.contentResolver
                val collection =
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI //MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val downloadedUri =
                    contentResolver.insert(collection, contentValues)
                Timber.tag("+++Q+++").d("downloadedUri: $downloadedUri")
                downloadedUri?.let { uri ->
                    copyFileData(contentResolver, uri, destFile)
                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    contentResolver.update(downloadedUri, contentValues, null, null)
                }

                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_FINISHED, downloadedUri))
                Timber.tag("+++Q+++").d("downloaded finished")
            } else {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val f = File(destFile.path)
                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun copyFileData(
        contentResolver: ContentResolver,
        destinationContentUri: Uri,
        fileToExport: File
    ) {
        try {
            Timber.d("copyFileData starts")
            Timber.d("fileToExport size: ${fileToExport.length()}")
            contentResolver.openFileDescriptor(destinationContentUri, "w")
                .use { parcelFileDescriptor ->
                    ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor)
                        .write(fileToExport.readBytes())
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @Throws(SecurityException::class)
    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        Timber.d("saveFileFromUri starts. Uri: $uri")
        Timber.d("saveFileFromUri destinationPath: $destinationPath")
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        if (destinationPath.isNullOrEmpty()) {
            return
        }

        val file = File(destinationPath)
        if (file.exists()) {
            file.delete()
        }
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            `is`?.copyTo(bos)
        } catch (e: java.lang.Exception) {
            Timber.d("saveFileFromUri: uri:$uri, error: $e")
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                Timber.d("saveFileFromUri: uri:$uri, error: $e")
            }

        }
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