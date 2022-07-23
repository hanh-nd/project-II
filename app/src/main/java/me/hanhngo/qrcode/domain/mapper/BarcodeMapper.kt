package me.hanhngo.qrcode.domain.mapper

import me.hanhngo.qrcode.R
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.domain.BarcodeItem
import me.hanhngo.qrcode.domain.schema.*
import me.hanhngo.qrcode.util.extension.toLocalDateTime
import me.hanhngo.qrcode.util.parseSchema

fun fromBarcodeEntityToBarcodeItem(barcodeEntity: BarcodeEntity): BarcodeItem {
    val id = barcodeEntity.id
    val content: String
    val resId: Int
    val dateTime = barcodeEntity.date.toLocalDateTime()
    val rawValue = barcodeEntity.text
    val format = barcodeEntity.format

    when (barcodeEntity.schema) {
        BarcodeSchema.EMAIL -> {
            val email = parseSchema(barcodeEntity.text) as Email
            content = email.email.toString()
            resId = R.drawable.ic_email
        }

        BarcodeSchema.PHONE -> {
            val phone = parseSchema(barcodeEntity.text) as Phone
            content = phone.phone
            resId = R.drawable.ic_phone
        }

        BarcodeSchema.URL -> {
            val url = parseSchema(barcodeEntity.text) as Url
            content = url.url
            resId = R.drawable.ic_url
        }

        BarcodeSchema.WIFI -> {
            val wifi = parseSchema(barcodeEntity.text) as Wifi
            content = wifi.name.toString()
            resId = R.drawable.ic_wifi
        }

        BarcodeSchema.SMS -> {
            val sms = parseSchema(barcodeEntity.text) as Sms
            content = sms.phone.toString()
            resId = R.drawable.ic_sms
        }

        BarcodeSchema.CUSTOM -> {
            val custom = parseSchema(barcodeEntity.text) as Custom
            content = custom.studentId.toString()
            resId = R.drawable.ic_custom
        }

        BarcodeSchema.OTHER -> {
            val text = parseSchema(barcodeEntity.text) as Other
            content = text.text
            resId = R.drawable.ic_text
        }
    }
    return BarcodeItem(id, resId, content, dateTime, rawValue, format)
}

fun fromBarcodeEntityListToBarcodeItemList(barcodeEntities: List<BarcodeEntity>): List<BarcodeItem> {
    return barcodeEntities.map {
        fromBarcodeEntityToBarcodeItem(it)
    }
}