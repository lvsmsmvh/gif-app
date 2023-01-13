package com.example.gifapp.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.example.gifapp.R
import com.example.gifapp.domain.entities.GifPicture
import java.io.File

object MediaSaverUtil {

    private fun getFileNameFor(gifPicture: GifPicture): String {
        return "Image_${gifPicture.id}"
    }
    fun getUriFromDisplayName(context: Context, displayName: String): Uri? {
        val projection: Array<String> = arrayOf(MediaStore.Files.FileColumns._ID)

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
            MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?", arrayOf(displayName), null
        )!!
        cursor.moveToFirst()
        return if (cursor.count > 0) {
            val columnIndex = cursor.getColumnIndex(projection[0])
            val fileId = cursor.getLong(columnIndex)
            cursor.close()
            Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + fileId)
        } else {
            null
        }
    }
    fun removeGif(context: Context, gifPicture: GifPicture) {
        logDebug("Deleting ${gifPicture.id} ...")

        val displayName = getFileNameFor(gifPicture)
        val uri = getUriFromDisplayName(context, displayName)
        if (uri != null) {
            val resolver: ContentResolver = context.contentResolver
            val selectionArgsPdf = arrayOf(displayName)
            try {
                resolver.delete(
                    uri,
                    MediaStore.Files.FileColumns.DISPLAY_NAME + "=?",
                    selectionArgsPdf
                )
                logDebug("Deleted successfully ${gifPicture.id}")
            } catch (ex: Exception) {
                ex.printStackTrace()
                // show some alert message
                logDebug("Deleting failed ${gifPicture.id}")
                logDebug(ex.message.toString())
            }
        }


    }

    fun removeGif(localUrl: String) {
        logDebug("File deleting $localUrl")
        val file = File(localUrl)
        file.delete()
    }


    /**
     * Returns a path for the new saved file.
     **/

    fun saveGif(context: Context, bytes: ByteArray, gifPicture: GifPicture): String {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileNameFor(gifPicture))
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/gif")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val appName = context.getString(R.string.app_name)
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$appName")
        }

        val contentResolver = context.contentResolver
        val gifContentUri: Uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )!!

        val path = getPath(context, gifContentUri)!!
        val outputStream = contentResolver.openOutputStream(gifContentUri, "w")!!

        outputStream.write(bytes, 0, bytes.size)
        outputStream.close()
        logDebug("File saving $path")
        return path
    }

    private fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else { // non-primary volumes e.g sd card
                    var filePath = "non"
                    //getExternalMediaDirs() added in API 21
                    val extenal = context.externalMediaDirs
                    for (f in extenal) {
                        filePath = f.absolutePath
                        if (filePath.contains(type)) {
                            val endIndex = filePath.indexOf("Android")
                            filePath = filePath.substring(0, endIndex) + split[1]
                        }
                    }
                    filePath
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (_: java.lang.Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}