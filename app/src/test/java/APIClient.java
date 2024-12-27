import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APIClient {

    private final String apiKey;
    private final Gson gson;

    public APIClient(String apiKey) {
        this.apiKey = apiKey;
        this.gson = new Gson();
    }

    // 内部静态类定义消息结构
    static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // 内部静态类定义请求体结构
    static class RequestBody {
        String model;
        Message[] messages;

        public RequestBody(String model, Message[] messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    // 核心方法：发送请求
    public String sendRequest(String apiUrl, String model, Message[] messages) throws Exception {
        // 构造请求体
        RequestBody requestBody = new RequestBody(model, messages);

        // 将请求体转换为 JSON
        String jsonInputString = gson.toJson(requestBody);

        // 创建 URL 对象
        URL url = new URL(apiUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为 POST
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + apiKey);

        // 启用输入输出流
        httpURLConnection.setDoOutput(true);

        // 写入请求体
        try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 获取响应码
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        // 读取响应体
        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 返回完整的 JSON 响应字符串
            return response.toString();
        }
    }

    public static void main(String[] args) {
        try {
            // 设置 API Key
            String apiKey = "sk-eab2838a42bc4090a6fdee30392d19bb"; // 替换为你的 API Key

            // 初始化 APIClient
            APIClient client = new APIClient(apiKey);

            // 构造多轮对话消息
            Message[] messages = new Message[]{
                    new Message("system", "You are a helpful assistant."),
                    new Message("user", "你好，请介绍下你自己。"),
                    new Message("assistant", "我是一个智能助手，可以回答您的问题。"),
                    new Message("user", "将这句话分成两句。"),
                    new Message("assistant", "好的，那我将这句话分成两句：我是一个智能助手，可以为您提供帮助。我可以回答您的问题，满足您的需求。"),
                    new Message("user", "合并成一句简单的话。"),

            };

            // 调用 sendRequest 方法，获取 JSON 响应
            String jsonResponse = client.sendRequest(
                    "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", // API 地址
                    "qwen-plus",                      // 模型名称
                    messages                          // 多轮对话消息
            );

            // 输出 JSON 响应
            System.out.println("Full JSON Response: " + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }
    }
}
