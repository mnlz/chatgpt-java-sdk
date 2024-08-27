package com.edu.openai.session.defaults;


import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.parameter.*;

import com.edu.openai.session.Configuration;
import com.edu.openai.session.OpenAiSession;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zzw
 * DefaultOpenAiSession 负责编写具体的实现方法
 */
public class DefaultOpenAiSession implements OpenAiSession {

    private final Configuration configuration;
    private final Map<String, Executor> executorGroup;


    public DefaultOpenAiSession(Configuration configuration,Map<String, Executor> executorGroup) {
        this.configuration = configuration;
        this.executorGroup = executorGroup;

    }


    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        //获取模型对应的执行器
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) throw new RuntimeException(completionRequest.getModel() + " 模型执行器尚未实现！");
        return executor.completions(completionRequest,eventSourceListener);


    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        //获取模型对应的执行器
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) throw new RuntimeException(completionRequest.getModel() + " 模型执行器尚未实现！");
        return executor.completions(apiHostByUser, apiKeyByUser,completionRequest,eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(CompletionRequest completionRequest) throws Exception {
        return completions(null, null, completionRequest);
    }

    @Override
    public CompletableFuture<String> completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest) throws Exception {
        //用于异步任务并获取结果
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();
        completions(completionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    future.complete(dataBuffer.toString());
                    return;
                }
                CompletionResponse chatCompletionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice choice : choices) {
                    Message delta = choice.getDelta();
                    // if (CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;
                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equalsIgnoreCase(finishReason)) {
                        future.complete(dataBuffer.toString());
                        return;
                    }
                    // 填充数据
                    dataBuffer.append(delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });
        return future;
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        return executorGroup.get(imageRequest.getModel()).genImages(imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        return executorGroup.get(imageRequest.getModel()).genImages(apiHostByUser, apiKeyByUser, imageRequest);
    }

    @Override
    public OkHttpClient getClient() {
        return configuration.getOkHttpClient();
    }
}