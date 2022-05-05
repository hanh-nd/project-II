package me.hanhngo.qrcode.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(barcodeEntity: BarcodeEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(barcodeEntities: List<BarcodeEntity>)

    @Query("SELECT * from barcodes")
    fun getAll(): Flow<List<BarcodeEntity>>

    @Query("SELECT * FROM barcodes WHERE isGenerated is 1")
    fun getGeneratedBarcodes(): Flow<List<BarcodeEntity>>

    @Query("SELECT * FROM barcodes WHERE id = :id")
    fun getDetail(id: Long): Flow<BarcodeEntity>

    @Query("DELETE FROM barcodes")
    suspend fun deleteAll()
}