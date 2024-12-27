package com.ml.shubham0204.docqa.domain.search

data class SearchResponse(
    val results: List<List<Result>> // 嵌套数组
) {
    data class Result(
        val id: Int,              // ID 字段
        val distance: Float,      // 匹配距离
        val entity: Entity        // 嵌套的实体对象
    )

    data class Entity(
        val text: String,         // 文档内容
        val subject: String       // 文档主题
    )
}

