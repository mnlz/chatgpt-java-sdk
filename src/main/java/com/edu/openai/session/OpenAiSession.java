package com.edu.openai.session;

import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionRequest;
import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionResponse;
import com.edu.openai.executor.parameter.CompletionRequest;
import com.edu.openai.executor.parameter.ImageRequest;
import com.edu.openai.executor.parameter.ImageResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author 小傅哥，微信：fustack
 * @description OpenAi 会话接口
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */

// 负责定义OpenAiSession中默认的方法
public interface OpenAiSession {

    /**
     * 问答模式，流式反馈
     *
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     * @throws Exception 异常
     */
    EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception;

    /**
     * 问答模式，流式反馈 & 接收用户自定义 apiHost、apiKey - 适用于每个用户都有自己独立配置的场景
     *
     * @param apiHostByUser       apiHost
     * @param apiKeyByUser        apiKey
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     * @throws Exception 异常
     */
    EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception;

    /**
     * 问答模式，同步响应 - 对流式回答数据的同步处理
     *
     * @param completionRequest 请求信息
     * @return 应答结果
     * @throws Exception 异常
     */
    CompletableFuture<String> completions(CompletionRequest completionRequest) throws Exception;

    /**
     * 问答模式，同步响应 - 对流式回答数据的同步处理
     *
     * @param apiHostByUser     apiHost
     * @param apiKeyByUser      apiKey
     * @param completionRequest 请求信息
     * @return 应答结果
     * @throws Exception 异常
     */
    CompletableFuture<String> completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest) throws Exception;

    /**
     * 生成图片
     *
     * @param imageRequest 图片描述
     * @return 应答结果
     */
    ImageResponse genImages(ImageRequest imageRequest) throws Exception;

    /**
     * 生成图片
     *
     * @param apiHostByUser apiHost
     * @param apiKeyByUser  apiKey
     * @param imageRequest  图片描述
     * @return 应答结果
     */
    ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception;

    OkHttpClient getClient();
}
