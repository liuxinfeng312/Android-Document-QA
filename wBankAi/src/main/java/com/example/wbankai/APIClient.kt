package com.example.wbankai

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class APIClient(private val apiKey: String) {

    private val gson: Gson = Gson()

    // 数据类定义消息结构
    data class Message(val role: String, val content: String)

    // 数据类定义请求体结构
    data class RequestBody(val model: String, val messages: Array<Message>)

    // 核心方法：发送请求
    @Throws(Exception::class)
    fun sendRequest(apiUrl: String, model: String, messages: Array<Message>): String {
        // 构造请求体
        val requestBody = RequestBody(model, messages)

        // 将请求体转换为 JSON
        val jsonInputString = gson.toJson(requestBody)

        // 创建 URL 对象
        val url = URL(apiUrl)
        val httpURLConnection = url.openConnection() as HttpURLConnection

        // 设置请求方法为 POST
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8")
        httpURLConnection.setRequestProperty("Accept", "application/json")
        httpURLConnection.setRequestProperty("Authorization", "Bearer $apiKey")

        // 启用输入输出流
        httpURLConnection.doOutput = true

        // 写入请求体
        httpURLConnection.outputStream.use { os ->
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
        httpURLConnection.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { br ->
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim())
                }
            }
        }

        // 返回完整的 JSON 响应字符串
        return response.toString()
    }
}

