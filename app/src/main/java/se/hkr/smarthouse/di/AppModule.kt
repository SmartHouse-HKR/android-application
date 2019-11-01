package se.hkr.androidjetpack.di

import android.app.Application
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.hkr.smarthouse.R
import se.hkr.smarthouse.persistence.AccountCredentialsDao
import se.hkr.smarthouse.persistence.AppDatabase
import se.hkr.smarthouse.persistence.AppDatabase.Companion.DATABASE_NAME
import se.hkr.smarthouse.util.Constants
import se.hkr.smarthouse.util.LiveDataCallAdapterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gson: Gson): Retrofit.Builder {
        return Retrofit
            .Builder()
            .baseUrl(Constants.WEB_API_BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

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
        return appDatabase.getLoginCredentialsDao()
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