package com.ml.shubham0204.docqa

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ml.shubham0204.docqa.ui.screens.ChatScreen
import com.ml.shubham0204.docqa.ui.screens.DocsScreen
import com.ml.shubham0204.docqa.domain.ocr.OcrManager
import com.ml.shubham0204.docqa.domain.search.ApiClient
import com.ml.shubham0204.docqa.domain.search.QueryRequest
import com.ml.shubham0204.docqa.domain.search.SearchResponse
//import com.ml.shubham0204.docqa.domain.search.SearchViewModel
import com.ml.shubham0204.docqa.ui.screens.OcrScreen

class MainActivity : ComponentActivity() {

//    private val searchViewModel: SearchViewModel by viewModels()

    // 初始化 ocrManager
    private lateinit var ocrManager: OcrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 初始化 OcrManager

        super.onCreate(savedInstanceState) // 这个调用应该在上面进行
//        // 示例：调用搜索功能
//        searchViewModel.searchDocuments(
//            collectionName = "demo_collection",
//            queryText = "火线",
//            filter = "subject == 'history'",
//            limit = 2,
//            onSuccess = { results ->
//                // 显示搜索结果
//                results.forEach {
//                    Toast.makeText(
//                        this,
//                        "ID: ${it.id}, Text: ${it.text}, Subject: ${it.subject}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            },
//            onError = { error ->
//                // 显示错误信息
//                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
//            }
//        )




        ocrManager = OcrManager(this)





        // 启用沉浸式状态栏
        enableEdgeToEdge()

        setContent {
            val navHostController = rememberNavController()

            NavHost(
                navController = navHostController,
                startDestination = "chat",
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            )
            {
                composable("docs") {
                    DocsScreen(onBackClick = { navHostController.navigateUp() })
                }
                composable("ocr") {
                    OcrScreen(onBackClick = { navHostController.navigateUp() }, ocrManager = ocrManager)
                }
                composable("chat") {
                    ChatScreen(
                        onOpenDocsClick = { navHostController.navigate("docs") },
                        onOcrClick = { startOcr() }, // OCR 按钮点击事件
                        onOcrScreenClick = { navHostController.navigate("ocr") }
                    )
                }
            }
        }
    }

    private fun searchDocuments() {
        val api = ApiClient.milvusApi

        val request = QueryRequest(
            collection_name = "demo_collection",
            query_text = "first document",
            limit = 3
        )
        api.searchDocuments(request).enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(call: retrofit2.Call<SearchResponse>, response: retrofit2.Response<SearchResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    // 展平嵌套数组
                    val flattenedResults = response.body()?.results?.flatten() ?: emptyList()

                    // 遍历每个结果
                    flattenedResults.forEach { result ->
                        val text = result.entity.text
                        val subject = result.entity.subject
                        val distance = result.distance
                        Log.d("Milvus", "Text: $text, Subject: $subject, Distance: $distance")
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

    private fun startOcr() {
        try {
            searchDocuments()
//
//            // 示例：调用搜索功能
//            searchViewModel.searchDocuments(
//                collectionName = "demo_collection",
//                queryText = "火线",
//                filter = "subject == 'history'",
//                limit = 2,
//                onSuccess = { results ->
//                    // 显示搜索结果
//                    results.forEach {
//                        Toast.makeText(
//                            this,
//                            "ID: ${it.id}, Text: ${it.text}, Subject: ${it.subject}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                },
//                onError = { error ->
//                    // 显示错误信息
//                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
//
//                }
//            )




            // 从 assets 文件夹中加载图片（确保图片在 assets 文件夹内）
            val inputStream = assets.open("test.jpg") // 确保文件名和路径正确
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 调用 OCR 识别功能
            ocrManager.runOcr(
                bitmap = bitmap,
                onSuccess = { result ->
                    Toast.makeText(this, "OCR Result: $result", Toast.LENGTH_LONG).show()
                },
                onError = { error ->
                    Toast.makeText(this, "OCR Failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        } catch (e: Exception) {
            // 错误处理
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放 OCR 资源
        ocrManager.release()
    }
}
