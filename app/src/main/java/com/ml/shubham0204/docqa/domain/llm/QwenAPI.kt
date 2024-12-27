package com.ml.shubham0204.docqa.domain.llm

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import org.koin.core.annotation.Single
import kotlinx.coroutines.runBlocking

@Single
class QwenAPI {
    private val apiKey = "sk-eab2838a42bc4090a6fdee30392d19bb" // 替换为你的 API Key
    private val gson: Gson = Gson()

    // 数据类定义消息结构
    data class Message(val role: String, val content: String)

    // 数据类定义请求体结构
    data class RequestBody(val model: String, val messages: Array<Message>)

    // 核心方法：发送请求
    suspend fun chat(model: String, systemMessage: String, userMessage: String): String {
        return withContext(Dispatchers.IO) {
            // 构造请求体
            val requestBody = RequestBody(
                model,
                arrayOf(
                    Message("system", systemMessage),
                    Message("user", userMessage)
                )
            )

            // 将请求体转换为 JSON
            val jsonInputString = gson.toJson(requestBody)

            // 创建 URL 对象
            val url = URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
            val httpURLConnection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
                doOutput = true
            }

            // 写入请求体
            httpURLConnection.outputStream.use { os: OutputStream ->
                val input = jsonInputString.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            // 获取响应码
            val responseCode = httpURLConnection.responseCode
            if (responseCode != 200) {
                throw RuntimeException("HTTP error code: $responseCode")
            }

            // 读取响应体
            val response = StringBuilder()
            httpURLConnection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { br: BufferedReader ->
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim())
                }
            }

            // 解析响应体
            println("Response Body: $response")
            val jsonObject = JsonParser.parseString(response.toString()).asJsonObject

            // 获取 choices[0].message.content 的值

            jsonObject.getAsJsonArray("choices")
                .get(0).asJsonObject
                .getAsJsonObject("message")
                .get("content").asString
        }
    }
}



fun main() = runBlocking {
    try {
        // 初始化 QwenAPI
        val client = QwenAPI()

        // 调用 chat 方法
        val response = client.chat(
            "qwen-7b-chat",                      // 模型名称
            "You are a helpful assistant.",   // 系统消息
            "你是谁？"                          // 用户消息
        )

        // 输出响应
        println("AI Response: $response")
    } catch (e: Exception) {
        e.printStackTrace() // 捕获并处理异常
    }
}
