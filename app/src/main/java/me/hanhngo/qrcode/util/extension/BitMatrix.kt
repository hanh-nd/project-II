package me.hanhngo.qrcode.util.extension

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.google.zxing.common.BitMatrix

fun BitMatrix.toBitmap(): Bitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        for (x in 0 until width) {
            for (y in 0 until height) {
                setPixel(x, y, if (get(x, y)) BLACK else WHITE)
            }
        }
    }
}