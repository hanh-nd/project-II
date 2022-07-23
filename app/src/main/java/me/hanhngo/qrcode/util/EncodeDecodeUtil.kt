package me.hanhngo.qrcode.util

import java.util.*

fun encode(input: String): String {
    val encoder = Base64.getEncoder()
    return encoder.encodeToString(input.toByteArray(Charsets.UTF_8))
}

fun decode(input: String): String {
    val decoder = Base64.getDecoder()
    return decoder.decode(input).toString(Charsets.UTF_8)
}
