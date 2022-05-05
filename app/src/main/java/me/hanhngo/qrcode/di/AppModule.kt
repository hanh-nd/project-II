package me.hanhngo.qrcode.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.hanhngo.qrcode.core.MyApplication
import me.hanhngo.qrcode.data.db.BarcodeDao
import me.hanhngo.qrcode.data.db.BarcodeDatabase
import me.hanhngo.qrcode.repository.BarcodeRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): MyApplication {
        return app as MyApplication
    }

    @Provides
    @Singleton
    fun provideDatabase(application: MyApplication): BarcodeDatabase {
        return Room.databaseBuilder(application, BarcodeDatabase::class.java, "barcodedatabase.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideBarcodeDao(database: BarcodeDatabase): BarcodeDao {
        return database.barcodeDao()
    }

    @Provides
    @Singleton
    fun provideCameraRepository(barcodeDao: BarcodeDao): BarcodeRepository {
        return BarcodeRepository(barcodeDao)
    }
}