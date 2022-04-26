package com.masoudjafari.myapplication.di

import android.content.Context
import androidx.room.Room
import com.masoudjafari.myapplication.data.source.DefaultCurrencyRepository
import com.masoudjafari.myapplication.data.source.local.CurrencyLocalDataSource
import com.masoudjafari.myapplication.data.source.remote.RetrofitService
import com.masoudjafari.myapplication.data.source.CurrencyDataSource
import com.masoudjafari.myapplication.data.source.CurrencyRepository
import com.masoudjafari.myapplication.data.source.local.CurrencyDatabase
import com.masoudjafari.myapplication.data.source.remote.CurrencyRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://api.exchangeratesapi.io/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): RetrofitService = retrofit.create(RetrofitService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteCurrencyDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalCurrencyDataSource

    @Singleton
    @RemoteCurrencyDataSource
    @Provides
    fun provideCurrencyRemoteDataSource(
        retrofitService: RetrofitService,
        ioDispatcher: CoroutineDispatcher
    ): CurrencyDataSource {
        return CurrencyRemoteDataSource(
            retrofitService,
            ioDispatcher
        )
    }

    @Singleton
    @LocalCurrencyDataSource
    @Provides
    fun provideCurrencyLocalDataSource(
        database: CurrencyDatabase,
        ioDispatcher: CoroutineDispatcher
    ): CurrencyDataSource {
        return CurrencyLocalDataSource(
            database.currencyExchangeRateDao(),
            database.balanceDao(),
            database.transactionDao(),
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CurrencyDatabase::class.java,
            "Currencies.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
object CurrencyRepositoryModule {

    @Singleton
    @Provides
    fun provideCurrencyRepository(
        @AppModule.RemoteCurrencyDataSource remoteCurrencyDataSource: CurrencyDataSource,
        @AppModule.LocalCurrencyDataSource localCurrencyDataSource: CurrencyDataSource
    ): CurrencyRepository {
        return DefaultCurrencyRepository(
            remoteCurrencyDataSource, localCurrencyDataSource
        )
    }
}