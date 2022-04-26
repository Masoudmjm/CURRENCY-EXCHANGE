package com.masoudjafari.myapplication.data.source.remote

import com.masoudjafari.myapplication.data.source.model.Response
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface RetrofitService {

    @GET("latest?access_key=c62bc65b51eed73277e6785fdfb9cd3a")
    suspend fun getLatest() : Response
}