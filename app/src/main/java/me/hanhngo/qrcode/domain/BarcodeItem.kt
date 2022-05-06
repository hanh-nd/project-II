package me.hanhngo.qrcode.domain

import androidx.annotation.DrawableRes
import me.hanhngo.qrcode.R
import java.time.LocalDateTime

data class BarcodeItem(
    val id: Long,
    @DrawableRes
    val resId: Int = R.drawable.ic_email,
    val content: String,
    val dateTime: LocalDateTime,
    val rawValue: String
)