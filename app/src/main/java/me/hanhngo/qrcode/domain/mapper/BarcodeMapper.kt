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
    val createdDate = barcodeEntity.date.toLocalDateTime()
    val rawValue = barcodeEntity.text
    val format = barcodeEntity.format

    when (val schema = parseSchema(barcodeEntity.text)) {
        is Email -> {
            content = schema.email.toString()
            resId = R.drawable.ic_email
        }

        is Phone -> {
            content = schema.phone
            resId = R.drawable.ic_phone
        }

        is Url -> {
            content = schema.url
            resId = R.drawable.ic_url
        }

        is Wifi -> {
            content = schema.name.toString()
            resId = R.drawable.ic_wifi
        }

        is Sms -> {
            content = schema.phone.toString()
            resId = R.drawable.ic_sms
        }

        is Custom -> {
            content = schema.studentId.toString()
            resId = R.drawable.ic_custom
        }

        else -> {
            content = (schema as Other).text
            resId = R.drawable.ic_text
        }
    }
    return BarcodeItem(id, resId, content, createdDate, rawValue, format)
}

fun fromBarcodeEntityListToBarcodeItemList(barcodeEntities: List<BarcodeEntity>): List<BarcodeItem> {
    return barcodeEntities.map {
        fromBarcodeEntityToBarcodeItem(it)
    }
}