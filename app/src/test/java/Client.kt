
class Client(){}
fun main() {
    try {
        // 设置 API Key
        val apiKey = "sk-eab2838a42bc4090a6fdee30392d19bb" // 替换为你的 API Key

        // 初始化 APIClient
        val client = APIClient(apiKey)

        // 构造多轮对话消息
        val messages = arrayOf(
            APIClient.Message("system", "You are a helpful assistant."),
            APIClient.Message("user", "你好，请介绍下你自己。"),
            APIClient.Message("assistant", "我是一个智能助手，可以回答您的问题。"),
            APIClient.Message("user", "将这句话分成两句。"),
            APIClient.Message("assistant", "好的，那我将这句话分成两句：我是一个智能助手，可以为您提供帮助。我可以回答您的问题，满足您的需求。"),
            APIClient.Message("user", "合并成一句简单的话。")
        )

        // 调用 sendRequest 方法，获取 JSON 响应
        val jsonResponse = client.sendRequest(
            "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", // API 地址
            "qwen-plus",                      // 模型名称
            messages                          // 多轮对话消息
        )

        // 输出 JSON 响应
        println("Full JSON Response: $jsonResponse")
    } catch (e: Exception) {
        e.printStackTrace() // 处理异常
    }
}