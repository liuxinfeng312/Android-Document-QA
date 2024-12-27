package com.ml.shubham0204.docqa.domain.search

data class InsertRequest(
    val collection_name: String,
    val docs: List<String>,
    val subject: String
)
