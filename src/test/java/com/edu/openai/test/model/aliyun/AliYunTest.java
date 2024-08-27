package com.edu.openai.test.model.aliyun;


import com.alibaba.fastjson.JSON;
import com.edu.openai.executor.model.aliyun.config.AliModelConfig;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.parameter.CompletionRequest;
import com.edu.openai.executor.parameter.CompletionResponse;
import com.edu.openai.executor.parameter.Message;
import com.edu.openai.session.Configuration;
import com.edu.openai.session.OpenAiSession;
import com.edu.openai.session.OpenAiSessionFactory;
import com.edu.openai.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AliYunTest {
    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() throws IOException {
        AliModelConfig aliModelConfig = new AliModelConfig();
        aliModelConfig.setApiHost("https://dashscope.aliyuncs.com/");
        aliModelConfig.setApiKey("sk-42969ef88e4e4ef6bc9539c9c2bedaaa");

        // 2. 配置文件
        Configuration configuration = new Configuration();
        configuration.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        configuration.setAliModelConfig(aliModelConfig);

        // 3. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 4. 开启会话
        this.openAiSession = factory.openSession();
    }

    /**
     * 文本 & 流式对话；选择不同的模型测试 GPT_3_5_TURBO、GPT_3_5_TURBO_1106、GPT_3_5_TURBO_16K、GPT_4、CHATGLM_TURBO
     */
    @Test
    public void test_completions() throws Exception {
        // 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content("你好，请介绍一下自己").build()))
                .model(CompletionRequest.Model.QWEN_TURBO.getCode())
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
                    if (CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

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
                .model(CompletionRequest.Model.QWEN_TURBO.getCode())
                .build();

        // 2. 同步响应
        CompletableFuture<String> future = openAiSession.completions(request);
        String response = future.get(500, TimeUnit.SECONDS);
        log.info("测试结果：{}", response);
    }
}
