package com.edu.openai.test.model.tencent;

import com.alibaba.fastjson.JSON;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.model.tencent.config.TencentConfig;
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
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class TencentTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() throws IOException {


        TencentConfig tencentConfig = new TencentConfig();
        tencentConfig.setApiHost("https://hunyuan.tencentcloudapi.com");
        tencentConfig.setSecretId("AKIDa6UlrqcFh3vb5fBYiicO1TnuoT5Xs36z");
        tencentConfig.setSecretKey("ofcTOOOMTadaWKLF0c6Vr4B9yjeYO9lV");

        // 2. 配置文件
        Configuration configuration = new Configuration();
        configuration.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        configuration.setTencentConfig(tencentConfig);

        // 3. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 4. 开启会话
        this.openAiSession = factory.openSession();
    }

    @Test
    public void test_completions() throws Exception {
        // 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(CompletionRequest.Role.USER).content("你好请介绍一下你自己吧").build()))
                .model(CompletionRequest.Model.HUNYUAN_CHATSTD.getCode())
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
}
