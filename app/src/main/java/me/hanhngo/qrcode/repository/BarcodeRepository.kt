package me.hanhngo.qrcode.repository

import kotlinx.coroutines.flow.Flow
import me.hanhngo.qrcode.data.db.BarcodeDao
import me.hanhngo.qrcode.data.db.BarcodeEntity
import javax.inject.Inject


class BarcodeRepository @Inject constructor(
    private val barcodeDao: BarcodeDao
) {
    suspend fun insert(barcodeEntity: BarcodeEntity) {
        barcodeDao.insert(barcodeEntity)
    }

    suspend fun insertAll(barcodeEntities: List<BarcodeEntity>) {
        barcodeDao.insertAll(barcodeEntities)
    }

    fun getAll(): Flow<List<BarcodeEntity>> {
        return barcodeDao.getAll()
    }

    fun getGeneratedBarcodes(): Flow<List<BarcodeEntity>> {
        return barcodeDao.getGeneratedBarcodes()
    }

    fun getDetail(id: Long): Flow<BarcodeEntity> {
        return barcodeDao.getDetail(id)
    }

    suspend fun deleteAll() {
        barcodeDao.deleteAll()
    }
}