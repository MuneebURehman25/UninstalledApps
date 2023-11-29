package com.recovery.recentlyuninstalledappsrecovery.hilt

import android.content.Context
import androidx.room.Room
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.domain.repository.AppInfoProvider
import com.recovery.recentlyuninstalledappsrecovery.domain.repository.AppInfoProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModule {


    @Provides
    @Singleton
    fun provideSystemRepository(): AppInfoProvider {
        return AppInfoProviderImpl()
    }


    @Singleton
    @Provides
    fun provideUninstallAppDao(@ApplicationContext context: Context): AppDatabase {

        return Room.databaseBuilder(context, AppDatabase::class.java,"uninstall_app_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}