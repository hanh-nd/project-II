package me.hanhngo.qrcode.domain.schema


enum class BarcodeSchema {
    EMAIL,
    PHONE,
    SMS,
    URL,
    WIFI,
    OTHER;
}

interface Schema {
    val schema: BarcodeSchema
    fun toBarcodeText(): String
}