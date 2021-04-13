package com.example.nimbbl.data.model.network

import com.example.nimbbl.data.model.model.createoder.CreateOrder_Model
import com.example.nimbbl.data.model.model.postbody.Catlogbody
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

interface ApiCall {


    @POST("orders/create")
    suspend fun creatOrder(@Body product_id: Catlogbody):retrofit2.Response<CreateOrder_Model>

    companion object{
        //    private const val BASE_URL = "http://8914a19e267b.ngrok.io/api/"
        private const val BASE_URL = "https://shop.nimbbl.tech/api/"
        private var retrofit: Retrofit? = null

        operator fun invoke(): ApiCall? {
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

            return retrofit!!.create(ApiCall::class.java)


        }
    }
}