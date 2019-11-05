package se.hkr.smarthouse.di

import android.app.Application
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import se.hkr.smarthouse.R
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.persistence.AppDatabase
import se.hkr.smarthouse.persistence.AppDatabase.Companion.DATABASE_NAME
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // Get correct db version if schema changed
            .build()
    }

    @Singleton
    @Provides
    fun provideAccountCredentialsDao(appDatabase: AppDatabase): AccountCredentialsDao {
        return appDatabase.accountCredentialsDao()
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide
            .with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}