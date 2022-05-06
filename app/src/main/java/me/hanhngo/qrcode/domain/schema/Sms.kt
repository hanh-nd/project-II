package me.hanhngo.qrcode.domain.schema

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.hanhngo.qrcode.util.extension.removePrefixIgnoreCase
import me.hanhngo.qrcode.util.extension.startsWithIgnoreCase

@Parcelize
class Sms(val phone: String?, val message: String?) : Schema, Parcelable {
    override val schema: BarcodeSchema
        get() = BarcodeSchema.SMS

    companion object {
        private const val PREFIX = "smsto:"
        private const val SEPARATOR = ":"

        fun parse(text: String): Sms? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val parts = text.removePrefixIgnoreCase(PREFIX).split(SEPARATOR)

            return Sms(
                phone = parts.getOrNull(0),
                message = parts.getOrNull(1)
            )
        }
    }

    override fun toBarcodeText(): String {
        return PREFIX +
                phone.orEmpty() +
                "$SEPARATOR${message.orEmpty()}"
    }
}