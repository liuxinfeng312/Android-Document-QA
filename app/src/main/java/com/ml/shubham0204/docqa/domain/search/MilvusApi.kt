package com.ml.shubham0204.docqa.domain.search

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MilvusApi {

    @GET("/")
    fun getRoot(): Call<Map<String, String>>

    @POST("/insert")
    fun insertDocuments(@Body request: InsertRequest): Call<Map<String, String>>

    @POST("/search")
    fun searchDocuments(@Body request: QueryRequest): Call<SearchResponse>
}
