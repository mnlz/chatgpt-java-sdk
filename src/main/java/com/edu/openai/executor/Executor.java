package com.edu.openai.executor;

import com.edu.openai.executor.parameter.CompletionRequest;
import com.edu.openai.executor.parameter.ImageRequest;
import com.edu.openai.executor.parameter.ImageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * 统一的模式执行器的入口
 * 规范定义每一个接入的模型，提供的方法
 * @author = zzw
 */
public interface Executor {




    /**
     * 实现流式对话的功能
     * @param completionRequest chat请求参数
     * @param eventSourceListener 监听服务器返回值的监听器
     * @return 返回 EventSource
     * @throws JsonProcessingException
     */
    EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException;


    /**
     * 实现自定义代理和apikey
     * @param apiHostByUser 自定义openAI代理ip
     * @param apiKeyByUser 自定义openAI的key
     *
     */
    EventSource completions(String apiHostByUser, String apiKeyByUser,CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException;




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


}
