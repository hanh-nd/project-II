package me.hanhngo.qrcode.util.extension

private val escapedRegex = """\\([\\;,":])""".toRegex()

fun String.unescape(): String {
    return replace(escapedRegex) { escaped ->
        escaped.groupValues[1]
    }
}

fun String.removeStartAll(symbol: Char): String {
    var newStart = 0

    run loop@ {
        forEachIndexed { index, c ->
            if (c == symbol) {
                newStart = index + 1
            } else {
                return@loop
            }
        }
    }

    return if (newStart >= length) {
        ""
    } else {
        substring(newStart)
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

fun List<String?>.joinToStringNotNullOrBlankWithLineSeparator(): String {
    return joinToStringNotNullOrBlank("\n")
}

fun List<String?>.joinToStringNotNullOrBlank(separator: String): String {
    return filter { it.isNullOrBlank().not() }.joinToString(separator)
}