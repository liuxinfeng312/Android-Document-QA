package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.QueryResult
import com.ml.shubham0204.docqa.data.RetrievedContext
import com.ml.shubham0204.docqa.domain.llm.QwenAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class QAUseCase(
//    private val documentsUseCase: DocumentsUseCase,
//    private val chunksUseCase: ChunksUseCase,
    private val qwenAPI: QwenAPI // 替换为 QwenAPI
) {

    fun getAnswer(query: String, prompt: String, onResponse: ((QueryResult) -> Unit)) {
        var jointContext = ""
        val retrievedContextList = ArrayList<RetrievedContext>()
//        chunksUseCase.getSimilarChunks(query, n = 5).forEach {
//            jointContext += " " + it.second.chunkData
//            retrievedContextList.add(RetrievedContext(it.second.docFileName, it.second.chunkData))
//        }
        Log.e("APP", "Context: $jointContext")
        val inputPrompt = prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)

        // 使用协程调用 QwenAPI
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("xfliu query :","=====================")
                Log.d("xfliu query :",query)

//                val llmResponse = qwenAPI.chat("qwen-plus", "You are a helpful assistant.", query)
                val response = qwenAPI.chat(
                    "qwen-plus",                      // 模型名称
                    "You are a helpful assistant.",   // 系统消息
                    query                          // 用户消息
                )
                Log.d("xfliu llm result :","=====================")
                Log.d("xfliu llm result :",response)
                onResponse(QueryResult(response))
            } catch (e: Exception) {
                Log.e("QAUseCase", "Error fetching response from QwenAPI: ${e.message}\nStackTrace: ${Log.getStackTraceString(e)}")
            }
        }
    }

//    fun canGenerateAnswers(): Boolean {
//        return documentsUseCase.getDocsCount() > 0
//    }
}
