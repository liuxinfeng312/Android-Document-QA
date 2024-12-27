package com.ml.shubham0204.docqa.domain.search

data class QueryRequest(
    val collection_name: String,
    val query_text: String,
//    val filter: String? = null,
    val limit: Int = 3
)
