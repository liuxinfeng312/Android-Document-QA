package com.ml.shubham0204.docqa.data


// 请求体模型
data class QueryRequest(
    val collection_name: String,
    val query_text: String,
    val filter: String? = null,
    val limit: Int = 2
)


// 响应体模型
data class QueryRes(
    val id: Int,
    val text: String,
    val subject: String
)

data class QueryResponse(
    val results: List<QueryRes>
)

