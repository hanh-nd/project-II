package me.hanhngo.qrcode.domain.schema

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.hanhngo.qrcode.util.extension.startsWithAnyIgnoreCase
import me.hanhngo.qrcode.util.extension.startsWithIgnoreCase

@Parcelize
class Url(val url: String) : Schema, Parcelable {

    companion object {
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"
        private const val WWW_PREFIX = "www."
        private val PREFIXES = listOf(HTTP_PREFIX, HTTPS_PREFIX, WWW_PREFIX)

        fun parse(text: String): Url? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }

            val url = when {
                text.startsWithIgnoreCase(WWW_PREFIX) -> "$HTTP_PREFIX$text"
                else -> text
            }

            return Url(url)
        }
    }

    override val schema: BarcodeSchema
        get() = BarcodeSchema.URL

    override fun toBarcodeText(): String = url
}