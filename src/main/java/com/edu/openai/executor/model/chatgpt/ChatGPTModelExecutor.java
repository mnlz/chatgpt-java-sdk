package com.edu.openai.executor.model.chatgpt;

import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.chatgpt.config.ChatGPTConfig;
import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionRequest;
import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTImageRequest;

import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTMessage;
import com.edu.openai.executor.parameter.*;
import com.edu.openai.executor.result.ResultHandler;
import com.edu.openai.session.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatGPTModelExecutor implements Executor, ParameterHandler<ChatGPTCompletionRequest>, ResultHandler {

    /**
     * 配置信息
     */
    private final ChatGPTConfig chatGPTConfig;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;
    /**
     * http 客户端
     */
    private final OkHttpClient okHttpClient;

    public ChatGPTModelExecutor(Configuration configuration) {
        this.chatGPTConfig = configuration.getChatGPTConfig();
        this.factory = configuration.createRequestFactory();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    //使用默认的apikey和apihostc
    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        return completions(null, null, completionRequest, eventSourceListener);
    }
    //使用用户自定义的apikey和apihost
    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        // 1. 核心参数校验；不对用户的传参做更改，只返回错误信息。
        if (!completionRequest.isStream()) {
            throw new RuntimeException("illegal parameter stream is false!");
        }

        // 2. 动态设置 Host、Key，便于用户传递自己的信息c
        String apiHost = null == apiHostByUser ? chatGPTConfig.getApiHost() : apiHostByUser;
        String apiKey = null == apiKeyByUser ? chatGPTConfig.getApiKey() : apiKeyByUser;

        // 3. 转换参数信息
        ChatGPTCompletionRequest chatGPTCompletionRequest = getParameterObject(completionRequest);

        // 4. 构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + apiKey)
                .url(apiHost.concat(chatGPTConfig.getV1_chat_completions()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), new ObjectMapper().writeValueAsString(chatGPTCompletionRequest)))
                .build();

        // 5. 返回事件结果
        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return genImages(null, null, imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {

        // 1. 统一转换参数
        ChatGPTImageRequest chatGPTImageRequest = ChatGPTImageRequest.builder()
                .n(imageRequest.getN())
                .size(imageRequest.getSize())
                .prompt(imageRequest.getPrompt())
                .model(imageRequest.getModel())
                .build();

        // 2. 动态设置 Host、Key，便于用户传递自己的信息
        String apiHost = null == apiHostByUser ? chatGPTConfig.getApiHost() : apiHostByUser;
        String apiKey = null == apiKeyByUser ? chatGPTConfig.getApiKey() : apiKeyByUser;

        // 构建请求信息
        Request request = new Request.Builder()
                // url: https://api.openai.com/v1/chat/completions 在chatGPTConfig中拿到配置
                .url(apiHost.concat(chatGPTConfig.getV1_images_completions()))
                .header("Authorization", "Bearer " + apiKey)
                // 封装请求参数信息，如果使用了 Fastjson 也可以替换 ObjectMapper 转换对象
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), new ObjectMapper().writeValueAsString(chatGPTImageRequest)))
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        ResponseBody body = execute.body();
        if (execute.isSuccessful() && body != null) {
            String responseBody = body.string();
           return  new ObjectMapper().readValue(responseBody, ImageResponse.class);
        } else {
            throw new IOException("Failed to get image response");
        }

    }


    @Override
    public ChatGPTCompletionRequest getParameterObject(CompletionRequest completionRequest) {
       // 统一的参数转换
        List<ChatGPTMessage> chatGPTMessageList = new LinkedList<>();
        List<Message> requestMessages = completionRequest.getMessages();
        for (Message message : requestMessages) {
            ChatGPTMessage chatGPTMessageVo = new ChatGPTMessage();
            chatGPTMessageVo.setContent(message.getContent());
            chatGPTMessageVo.setName(message.getName());
            chatGPTMessageVo.setRole(message.getRole());
            chatGPTMessageList.add(chatGPTMessageVo);
        }
        ChatGPTCompletionRequest chatGPTCompletionRequest = new ChatGPTCompletionRequest();
        chatGPTCompletionRequest.setModel(completionRequest.getModel());
        chatGPTCompletionRequest.setTemperature(completionRequest.getTemperature());
        chatGPTCompletionRequest.setStream(completionRequest.isStream());
        chatGPTCompletionRequest.setTopP(completionRequest.getTopP());
        chatGPTCompletionRequest.setMessages(chatGPTMessageList);
        return chatGPTCompletionRequest;

    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return eventSourceListener;
    }
}
