package me.hanhngo.qrcode.util.extension

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import me.hanhngo.qrcode.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

const val PNG = ".png"

fun Bitmap.save(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= 29) {
        val values = createContentValues()
        values.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            "Pictures/" + context.getString(R.string.app_name)
        )
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            try {
                saveImageToStream(context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    } else {
        val directory = File(
            Environment.getExternalStorageDirectory()
                .toString() + '/' + context.getString(R.string.app_name)
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val fileName = System.currentTimeMillis().toString() + PNG
        val file = File(directory, fileName)

        try {
            saveImageToStream(FileOutputStream(file))
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

private fun Bitmap.saveImageToStream(outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun createContentValues(): ContentValues {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }
    return values
}

