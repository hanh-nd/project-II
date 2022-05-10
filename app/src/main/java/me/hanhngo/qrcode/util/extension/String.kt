package me.hanhngo.qrcode.util.extension

import android.util.Patterns

private val escapedRegex = """\\([\\;,":])""".toRegex()

fun String.unescape(): String {
    return replace(escapedRegex) { escaped ->
        escaped.groupValues[1]
    }
}

fun String.removePrefixIgnoreCase(prefix: String): String {
    return substring(prefix.length)
}

fun String.startsWithIgnoreCase(prefix: String): Boolean {
    return startsWith(prefix, true)
}

fun String.startsWithAnyIgnoreCase(prefixes: List<String>): Boolean {
    prefixes.forEach { prefix ->
        if (startsWith(prefix, true)) {
            return true
        }
    }
    return false
}

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isUrl(): Boolean {
    return Patterns.WEB_URL.matcher(this).matches()
}

fun String.isPhone(): Boolean {
    return Patterns.PHONE.matcher(this).matches()
}

fun String.addPrefixString(string: String): String {
    return StringBuilder().append(string).append(this).toString()
}