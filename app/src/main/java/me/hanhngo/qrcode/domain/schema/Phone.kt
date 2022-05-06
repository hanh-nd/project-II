package me.hanhngo.qrcode.domain.schema

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.hanhngo.qrcode.util.extension.removePrefixIgnoreCase
import me.hanhngo.qrcode.util.extension.startsWithIgnoreCase


@Parcelize
class Phone(val phone: String) : Schema, Parcelable {

    companion object {
        private const val PREFIX = "tel:"

        fun parse(text: String): Phone? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val phone = text.removePrefixIgnoreCase(PREFIX)
            return Phone(phone)
        }
    }

    override val schema: BarcodeSchema
        get() = BarcodeSchema.PHONE

    override fun toBarcodeText(): String {
        return "$PREFIX$phone"
    }
}