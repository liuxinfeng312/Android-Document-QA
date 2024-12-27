package com.ml.shubham0204.docqa.domain.search

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000" // 替换为你的服务器地址

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val milvusApi: MilvusApi = retrofit.create(MilvusApi::class.java)
}
