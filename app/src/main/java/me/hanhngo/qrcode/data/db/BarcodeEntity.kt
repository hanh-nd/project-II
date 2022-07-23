package me.hanhngo.qrcode.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.zxing.BarcodeFormat
import kotlinx.parcelize.Parcelize
import me.hanhngo.qrcode.domain.schema.BarcodeSchema

@Entity(tableName = "barcodes")
@TypeConverters(BarcodeDatabaseTypeConverter::class)
@Parcelize
data class BarcodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "raw_text") val text: String,
    val schema: BarcodeSchema,
    val date: Long,
    @ColumnInfo(name = "is_generated") val isGenerated: Boolean = false,
    val format: BarcodeFormat,
) : Parcelable