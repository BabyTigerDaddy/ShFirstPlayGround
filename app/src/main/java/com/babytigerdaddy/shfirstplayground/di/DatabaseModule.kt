package com.babytigerdaddy.shfirstplayground.di

import android.content.Context
import androidx.room.Room
import com.babytigerdaddy.shfirstplayground.data.local.database.AppDatabase
import com.babytigerdaddy.shfirstplayground.data.local.database.HappyLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room DB 및 DAO Hilt 제공자.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

    @Provides
    fun provideHappyLogDao(database: AppDatabase): HappyLogDao = database.happyLogDao()

    private const val DB_NAME = "shfirstplayground.db"
}
