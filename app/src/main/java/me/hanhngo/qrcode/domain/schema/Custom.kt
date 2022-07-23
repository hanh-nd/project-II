package me.hanhngo.qrcode.domain.schema

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.hanhngo.qrcode.util.decode
import me.hanhngo.qrcode.util.encode
import me.hanhngo.qrcode.util.extension.removePrefixIgnoreCase
import me.hanhngo.qrcode.util.extension.startsWithIgnoreCase

@Parcelize
class Custom(val studentId: String?, val name: String?) : Schema, Parcelable {
    override val schema: BarcodeSchema
        get() = BarcodeSchema.CUSTOM

    companion object {
        private const val PREFIX = "HUST:"
        private const val SEPARATOR = ":"

        fun parse(text: String): Custom? {
            try {
                val rawText = decode(text)
                if (rawText.startsWithIgnoreCase(PREFIX).not()) {
                    return null
                }

                val parts = rawText.removePrefixIgnoreCase(PREFIX).split(SEPARATOR)

                return Custom(
                    studentId = parts.getOrNull(0),
                    name = parts.getOrNull(1)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun toBarcodeText(): String {
        return encode(
            PREFIX +
                    studentId.orEmpty() +
                    "$SEPARATOR${name.orEmpty()}"
        )
    }
}