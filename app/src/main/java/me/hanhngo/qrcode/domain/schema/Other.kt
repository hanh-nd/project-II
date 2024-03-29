package me.hanhngo.qrcode.domain.schema

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Other(val text: String) : Schema, Parcelable {
    override val schema: BarcodeSchema
        get() = BarcodeSchema.OTHER

    override fun toBarcodeText(): String = text
}