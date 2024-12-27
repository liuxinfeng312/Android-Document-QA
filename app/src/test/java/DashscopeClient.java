import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DashscopeClient {

    private final String apiKey;
    private final Gson gson;

    public DashscopeClient(String apiKey) {
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

    // 内部静态类定义响应结构
    static class Response {
        String id;
        String object;
        String created;
        String model;
        Choice[] choices;

        static class Choice {
            String text;
            String index;
            String finish_reason;
        }
    }

    // 核心方法：发送请求
    public String chat(String model, String systemMessage, String userMessage) throws Exception {
        // 构造请求体
        RequestBody requestBody = new RequestBody(
                model,
                new Message[]{
                        new Message("system", systemMessage),
                        new Message("user", userMessage)
                }
        );

        // 将请求体转换为 JSON
        String jsonInputString = gson.toJson(requestBody);

        // 创建 URL 对象
        URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
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
            System.out.println("Response Body: " + response);


            // 转换为 JsonObject
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

            // 获取 choices[0].message.content 的值
            String content = jsonObject.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // 输出 content
//            System.out.println("Content: " + content);
            return content;

        }
    }

    public static void main(String[] args) {
        try {
            // 1. 设置 API Key
            String apiKey = "sk-eab2838a42bc4090a6fdee30392d19bb"; // 替换为你的 API Key

            // 2. 初始化 DashscopeClient
            DashscopeClient client = new DashscopeClient(apiKey);

            // 3. 调用 chat 方法，获取 AI 响应
            String response = client.chat(
                    "qwen-plus",                      // 模型名称
                    "You are a helpful assistant.",   // 系统消息
                    "你是谁？"                          // 用户消息
            );

            // 4. 输出响应
            System.out.println("AI Response: " + response);
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }
    }
}

