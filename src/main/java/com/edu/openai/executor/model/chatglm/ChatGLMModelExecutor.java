package com.edu.openai.executor.model.chatglm;

import com.alibaba.fastjson.JSON;
import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.edu.openai.executor.model.chatglm.utils.BearerTokenUtils;
import com.edu.openai.executor.model.chatglm.valobj.ChatGLMCompletionRequest;

import com.edu.openai.executor.model.chatglm.valobj.ChatGLMMessage;

import com.edu.openai.executor.parameter.*;
import com.edu.openai.executor.result.ResultHandler;
import com.edu.openai.session.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ChatGLMModelExecutor implements Executor, ParameterHandler<ChatGLMCompletionRequest>, ResultHandler {

    /**
     * 配置信息
     */
    private final ChatGLMConfig chatGLMConfig;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;

    public ChatGLMModelExecutor(Configuration configuration) {
        this.chatGLMConfig = configuration.getChatGLMConfig();
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener listener) throws JsonProcessingException {

        return completions(null,null, completionRequest, eventSourceListener(listener));



    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {

        //1.转换参数
        ChatGLMCompletionRequest chatGLMCompletionRequest = getParameterObject(completionRequest);

        // 2. 自定义配置
        ChatGLMConfig chatGLMConfigByUser = new ChatGLMConfig();
        chatGLMConfigByUser.setApiHost(apiHostByUser);
        if (null != apiKeyByUser) {
            chatGLMConfigByUser.setApiSecretKey(apiKeyByUser);
        }
        String apiHost = chatGLMConfigByUser.getApiHost() == null ? chatGLMConfig.getApiHost() : chatGLMConfigByUser.getApiHost();
        String apiKey = chatGLMConfigByUser.getApiKey() == null ? chatGLMConfig.getApiKey() : chatGLMConfigByUser.getApiKey();
        String apiSecret = chatGLMConfigByUser.getApiSecret() == null ? chatGLMConfig.getApiSecret() : chatGLMConfigByUser.getApiSecret();

        //2.构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(apiKey, apiSecret))
                .url(apiHost.concat(chatGLMConfig.getChat_v4_completions()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), chatGLMCompletionRequest.toString()))
                .build();
        // 3. 返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));


    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return null;
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
        return null;
    }

    @Override
    public ChatGLMCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        ChatGLMCompletionRequest chatGLMCompletionRequest = new ChatGLMCompletionRequest();
        chatGLMCompletionRequest.setTemperature(completionRequest.getTemperature());
        List<ChatGLMMessage> chatGLMMessages = new ArrayList<>();
        List<Message> requestMessages = completionRequest.getMessages();

        for (Message message : requestMessages) {
            ChatGLMMessage chatGLMMessage = new ChatGLMMessage();
            chatGLMMessage.setContent(message.getContent());
            chatGLMMessage.setRole(message.getRole());
            chatGLMMessages.add(chatGLMMessage);
        }
        chatGLMCompletionRequest.setMessages(chatGLMMessages);
        chatGLMCompletionRequest.setStream(completionRequest.isStream());
        chatGLMCompletionRequest.setModel(chatGLMCompletionRequest.getModel());


        log.info("completionRequest:{}",completionRequest);
        log.info("chatGLMCompletionRequest:{}",chatGLMCompletionRequest);

        return chatGLMCompletionRequest;
    }
//{"id":"8439641316893948328","created":1709453054,"model":"glm-4","choices":[{"index":0,"delta":{"role":"assistant","content":"你好"}}]}
//{"id":"8439641316893948328","created":1709453054,"model":"glm-4","choices":[{"index":0,"finish_reason":"stop","delta":{"role":"assistant","content":""}}],"usage":{"prompt_tokens":10,"completion_tokens":72,"total_tokens":82}}
    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
       return  eventSourceListener;
    }
}
