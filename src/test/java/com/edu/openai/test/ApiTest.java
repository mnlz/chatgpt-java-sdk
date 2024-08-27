package com.edu.openai.test;

import com.alibaba.fastjson.JSON;

import com.edu.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.edu.openai.executor.model.chatgpt.config.ChatGPTConfig;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionRequest;
import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionResponse;
import com.edu.openai.executor.parameter.*;
import com.edu.openai.session.Configuration;
import com.edu.openai.session.OpenAiSession;
import com.edu.openai.session.OpenAiSessionFactory;
import com.edu.openai.session.defaults.DefaultOpenAiSessionFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 小傅哥，微信：fustack
 * @description 单元测试
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 *
 *  configuration.setApiHost("https://api.openai-proxy.com/");
 *  configuration.setApiKey("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");
 */
@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        ChatGPTConfig chatGPTConfig = new ChatGPTConfig();
        chatGPTConfig.setApiHost("https://api.openai-proxy.com/");
        chatGPTConfig.setApiKey("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");

        // 1. ChatGLM、ChatGPT 配置
        ChatGLMConfig chatGLMConfig = new ChatGLMConfig();
        chatGLMConfig.setApiHost("https://open.bigmodel.cn/");
        chatGLMConfig.setApiSecretKey("cf2f0b4d0e495be3e6c30698dc26b558.WZpFF6Li0xipmUKo");

        Configuration configuration = new Configuration();
        configuration.setChatGPTConfig(chatGPTConfig);
        configuration.setChatGLMConfig(chatGLMConfig);
        DefaultOpenAiSessionFactory defaultOpenAiSessionFactory = new DefaultOpenAiSessionFactory(configuration);
        this.openAiSession = defaultOpenAiSessionFactory.openSession();

    }



//{"id":"8439641316893948328","created":1709453054,"model":"glm-4","choices":[{"index":0,"delta":{"role":"assistant","content":"你好"}}]}
//{"id":"8439641316893948328","created":1709453054,"model":"glm-4","choices":[{"index":0,"finish_reason":"stop","delta":{"role":"assistant","content":""}}],"usage":{"prompt_tokens":10,"completion_tokens":72,"total_tokens":82}}
    @Test
    public void test_chat_completions() throws Exception {
        // 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content("你好，简单介绍一下自己").build()))
               // .model(CompletionRequest.Model.GPT_3_5_TURBO_1106.getCode())
                .model(CompletionRequest.Model.CHATGLM_4.getCode())
//                .model(CompletionRequest.Model.XUNFEI.getCode())
                .build();

        // 2. 请求等待
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 3. 应答请求
        EventSource eventSource = openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    log.info("OpenAI 应答完成");
                    return;
                }

                CompletionResponse chatCompletionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    // if (CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = chatChoice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equalsIgnoreCase(finishReason)) {
                        return;
                    }

                    log.info("测试结果：{}", delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @Test
    public void test_completions_future() throws Exception {
        // 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content("你好，介绍一下自己").build()))
                // .model(CompletionRequest.Model.GPT_3_5_TURBO_1106.getCode())
                .model(CompletionRequest.Model.CHATGLM_4.getCode())
                .build();

        // 2. 同步响应
        CompletableFuture<String> future = openAiSession.completions(request);
        String response = future.get(500, TimeUnit.SECONDS);
        log.info("测试结果：{}", response);
    }

    @Test
    public void testConcurrentCompletions() throws Exception {

        // 创建5个不同的消息请求
        List<String> messages = Arrays.asList("你好，介绍一下自己", "请问你如何处理并发请求？", "你对AI的未来有何看法？", "最近有什么有趣的项目吗？", "你能推荐一些学习资源吗？");

        // 使用CompletableFuture并行处理所有请求
        List<CompletableFuture<String>> futures = messages.stream()
                .map(message -> CompletionRequest.builder()
                        .stream(true)
                        .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content(message).build()))
                        .model(CompletionRequest.Model.CHATGLM_4.getCode())
                        .build())
                .map(request -> {
                    try {
                        return openAiSession.completions(request);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // 等待所有请求完成
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> System.out.println("所有请求已完成"));

        // 获取并处理每个请求的结果
        futures.forEach(future -> future.thenAccept(response -> System.out.println("请求结果: " + response)));

        // 确保在继续之前所有操作都已完成
        allDone.get(500, TimeUnit.SECONDS);
    }





}
