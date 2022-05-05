package me.hanhngo.qrcode.data.db

import androidx.room.TypeConverter
import com.google.zxing.BarcodeFormat
import me.hanhngo.qrcode.domain.schema.BarcodeSchema

class BarcodeDatabaseTypeConverter {
    @TypeConverter
    fun fromBarcodeFormat(barcodeFormat: BarcodeFormat): String {
        return barcodeFormat.name
    }

    @TypeConverter
    fun toBarcodeFormat(value: String): BarcodeFormat {
        return BarcodeFormat.valueOf(value)
    }

    @TypeConverter
    fun fromBarcodeSchema(barcodeSchema: BarcodeSchema): String {
        return barcodeSchema.name
    }

    @TypeConverter
    fun toBarcodeSchema(value: String): BarcodeSchema {
        return BarcodeSchema.valueOf(value)
    }
}