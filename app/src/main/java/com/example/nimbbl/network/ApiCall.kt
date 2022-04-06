package com.example.nimbbl.network

import android.util.Log
import com.example.nimbbl.model.GenerateDemoTokenResponse
import com.example.nimbbl.model.createoder.OrderDetailVo
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import java.io.IOException
import java.util.concurrent.TimeUnit

interface ApiCall {


    @POST
    suspend fun creatOrder(@Url url :String,@Header("Authorization") auth: String, @Body body: RequestBody):retrofit2.Response<OrderDetailVo>

    @POST
    suspend fun generateToken(@Url url: String,@Body body: RequestBody): retrofit2.Response<GenerateDemoTokenResponse>

    companion object{
        //    private const val BASE_URL = "http://8914a19e267b.ngrok.io/api/"
          var BASE_URL = "https://shop.nimbbl.tech/api/"
       // private const val BASE_URL = "https://uatshop.nimbbl.tech/api/"
        private var retrofit: Retrofit? = null

        operator fun invoke(): ApiCall? {
            val logging =  HttpLoggingInterceptor()
           logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                            .method(original.method, original.body)
                        val request = requestBuilder.build()
                        return chain.proceed(request)
                    }
                })
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build()
            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            Log.d("SAN", BASE_URL)

            return retrofit!!.create(ApiCall::class.java)


        }
    }
}