package com.ml.shubham0204.docqa.domain.search

import android.util.Log

class test {
}

private fun searchDocuments() {
    val api = ApiClient.milvusApi

    val request = QueryRequest(
        collection_name = "demo_collection",
        query_text = "first document",
        limit = 2
    )

    api.searchDocuments(request).enqueue(object : retrofit2.Callback<SearchResponse> {
        override fun onResponse(call: retrofit2.Call<SearchResponse>, response: retrofit2.Response<SearchResponse>) {
            if (response.isSuccessful && response.body() != null) {
                response.body()?.results?.forEach { result ->
                    Log.d("Milvus", "Text: ${result.toString()}, Subject: ${result.toString()}")
                }
            } else {
                Log.e("Milvus", "Search Failed: ${response.code()}")
            }
        }

        override fun onFailure(call: retrofit2.Call<SearchResponse>, t: Throwable) {
            Log.e("Milvus", "Search Error: ${t.message}")
        }
    })
}
fun main() {
    searchDocuments()
}