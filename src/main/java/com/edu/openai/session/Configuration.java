package com.edu.openai.session;

import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.aliyun.AliModelExecutor;
import com.edu.openai.executor.model.aliyun.config.AliModelConfig;
import com.edu.openai.executor.model.chatglm.ChatGLMModelExecutor;
import com.edu.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.edu.openai.executor.model.chatgpt.ChatGPTModelExecutor;
import com.edu.openai.executor.model.chatgpt.config.ChatGPTConfig;
import com.edu.openai.executor.model.tencent.TencentModelExecutor;
import com.edu.openai.executor.model.tencent.config.TencentConfig;
import com.edu.openai.executor.parameter.CompletionRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.HashMap;

/**
 * @author 小傅哥，微信：fustack
 * @description 配置信息
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    /**
     * OpenAi ChatGPT Config
     */
    private ChatGPTConfig chatGPTConfig;

    /**
     * 智谱Ai ChatGLM Config
     */
    private ChatGLMConfig chatGLMConfig;

    /**
     * 阿里通义千问
     */
    private AliModelConfig aliModelConfig;

    /**
     * 腾讯混元
     */
    private TencentConfig tencentConfig;


    private OkHttpClient okHttpClient;

    //存放整个执行器的Map
    private HashMap<String, Executor> executorGroup;

    public EventSource.Factory createRequestFactory(){
        return EventSources.createFactory(okHttpClient);
        }


    // OkHttp 配置信息
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;
    private long connectTimeout = 4500;
    private long writeTimeout = 4500;
    private long readTimeout = 4500;

    // http keywords
    public static final String SSE_CONTENT_TYPE = "text/event-stream";
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";
    public static final String APPLICATION_JSON = "application/json";
    public static final String JSON_CONTENT_TYPE = APPLICATION_JSON + "; charset=utf-8";

    public HashMap<String, Executor> newExecutorGroup() {
        this.executorGroup = new HashMap<>();

        //ChatGPT 执行器填充
        Executor chatGPTModelExecutor = new ChatGPTModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.GPT_3_5_TURBO.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.GPT_3_5_TURBO_1106.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.GPT_3_5_TURBO_16K.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.GPT_4.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.GPT_4_32K.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.DALL_E_2.getCode(), chatGPTModelExecutor);
        executorGroup.put(CompletionRequest.Model.DALL_E_3.getCode(), chatGPTModelExecutor);

        //ChatGLM 类型执行器填充
        ChatGLMModelExecutor chatGLMModelExecutor = new ChatGLMModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.CHATGLM_4.getCode(), chatGLMModelExecutor);
        executorGroup.put(CompletionRequest.Model.CHATGLM_4v.getCode(), chatGLMModelExecutor);
        executorGroup.put(CompletionRequest.Model.CHATGLM_3_TURBO.getCode(), chatGLMModelExecutor);

        // 阿里通义千问
        Executor aliModelExecutor = new AliModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.QWEN_TURBO.getCode(), aliModelExecutor);
        executorGroup.put(CompletionRequest.Model.QWEN_PLUS.getCode(), aliModelExecutor);
        executorGroup.put(CompletionRequest.Model.QWEN_MAX.getCode(), aliModelExecutor);

        // 腾讯混元
        Executor tencentExecutor = new TencentModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.HUNYUAN_CHATSTD.getCode(), tencentExecutor);
        executorGroup.put(CompletionRequest.Model.HUNYUAN_CHATPRO.getCode(), tencentExecutor);


        return this.executorGroup;
    }



}
