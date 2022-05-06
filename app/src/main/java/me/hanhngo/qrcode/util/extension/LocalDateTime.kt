package me.hanhngo.qrcode.util.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toFormattedString(): String {
    val format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(format)
}