package me.hanhngo.qrcode.util

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.datamatrix.DataMatrixWriter
import com.google.zxing.oned.*
import com.google.zxing.pdf417.PDF417Writer
import com.google.zxing.qrcode.QRCodeWriter
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.domain.schema.*
import me.hanhngo.qrcode.util.extension.toBitmap

fun parseContent(content: String, format: BarcodeFormat = BarcodeFormat.QR_CODE): Bitmap {
    val width: Int
    val height: Int
    val writer = when (format) {
        BarcodeFormat.AZTEC -> {
            width = 512
            height = 512
            AztecWriter()
        }

        BarcodeFormat.CODABAR -> {
            width = 512
            height = 170
            CodaBarWriter()
        }

        BarcodeFormat.CODE_39 -> {
            width = 512
            height = 170
            Code39Writer()
        }

        BarcodeFormat.CODE_93 -> {
            width = 512
            height = 170
            Code93Writer()
        }

        BarcodeFormat.CODE_128 -> {
            width = 512
            height = 170
            Code128Writer()
        }

        BarcodeFormat.DATA_MATRIX -> {
            width = 512
            height = 512
            DataMatrixWriter()
        }

        BarcodeFormat.EAN_8 -> {
            width = 512
            height = 170
            EAN8Writer()
        }

        BarcodeFormat.EAN_13 -> {
            width = 512
            height = 170
            EAN13Writer()
        }

        BarcodeFormat.ITF -> {
            width = 512
            height = 170
            ITFWriter()
        }

        BarcodeFormat.PDF_417 -> {
            width = 512
            height = 512
            PDF417Writer()
        }

        BarcodeFormat.QR_CODE -> {
            width = 512
            height = 512
            QRCodeWriter()
        }

        BarcodeFormat.UPC_A -> {
            width = 512
            height = 170
            UPCAWriter()
        }

        BarcodeFormat.UPC_E -> {
            width = 512
            height = 170
            UPCEWriter()
        }

        else -> throw IllegalArgumentException("No encoder available for format $format")
    }
    return writer.encode(content, format, width, height).toBitmap()
}

fun parseBarcode(barcode: Barcode): BarcodeEntity {
    val schema = parseSchema(barcode.rawValue.toString())
    val format = parseFormat(barcode.format)

    return BarcodeEntity(
        text = barcode.rawValue.toString(),
        schema = schema.schema,
        date = System.currentTimeMillis(),
        format = format
    )
}

fun parseSchema(text: String): Schema {
    return Email.parse(text)
        ?: Url.parse(text)
        ?: Phone.parse(text)
        ?: Sms.parse(text)
        ?: Wifi.parse(text)
        ?: Custom.parse(text)
        ?: Other(text)
}

fun parseFormat(format: Int): BarcodeFormat {
    return when (format) {
        Barcode.FORMAT_AZTEC -> BarcodeFormat.AZTEC
        Barcode.FORMAT_CODABAR -> BarcodeFormat.CODABAR
        Barcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
        Barcode.FORMAT_CODE_93 -> BarcodeFormat.CODE_93
        Barcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
        Barcode.FORMAT_DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
        Barcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
        Barcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
        Barcode.FORMAT_ITF -> BarcodeFormat.ITF
        Barcode.FORMAT_PDF417 -> BarcodeFormat.PDF_417
        Barcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
        Barcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
        Barcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E

        else -> throw IllegalArgumentException("Unknown format")
    }
}

fun parseListFormat(formatList: List<Int>): List<BarcodeFormat> {
    return formatList.map { parseFormat(it) }
}

