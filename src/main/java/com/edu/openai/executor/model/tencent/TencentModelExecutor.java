package com.edu.openai.executor.model.tencent;

import com.alibaba.fastjson.JSON;
import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.model.tencent.config.TencentConfig;
import com.edu.openai.executor.model.tencent.utils.SecurityUtils;
import com.edu.openai.executor.model.tencent.valobj.Model;
import com.edu.openai.executor.model.tencent.valobj.TencentCompletionRequest;
import com.edu.openai.executor.model.tencent.valobj.TencentCompletionResponse;
import com.edu.openai.executor.model.tencent.valobj.TencentMessage;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TencentModelExecutor implements Executor, ParameterHandler<TencentCompletionRequest>, ResultHandler {

    private static final String FINISH_REASON_STOP = "stop";

    private final EventSource.Factory factory;
    private final TencentConfig tencentConfig;

    public TencentModelExecutor(Configuration configuration) {
        this.tencentConfig = configuration.getTencentConfig();
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        //1.参数转换
        TencentCompletionRequest tencentCompletionRequest = getParameterObject(completionRequest);
        String model = Model.CHAT_PRO.getCode();
        String version = this.tencentConfig.getApiVersion();
        String region = this.tencentConfig.getRegion();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        String body = JSON.toJSONString(tencentCompletionRequest);
        String authorization = SecurityUtils.getAuthorization(body,timestamp,tencentConfig,model);
        Request request = new Request.Builder()
                .header("Authorization", authorization)
                .header("X-TC-Action", model)
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", version)
                .header("X-TC-Region", region)
                .header("Content-Type", Configuration.APPLICATION_JSON)
                .url(tencentConfig.getApiHost())
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), body))
                .build();
        log.info("request: " + request.headers());
        log.info("body:"+body);


        return factory.newEventSource(request,eventSourceListener(eventSourceListener));
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {


        return null;
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
    public TencentCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        List<TencentMessage> messages = completionRequest.getMessages().stream()
                .map(TencentMessage::of)
                .collect(Collectors.toList());
        return TencentCompletionRequest.builder()
                .messages(messages)
                .temperature(completionRequest.getTemperature())
                .topP(completionRequest.getTopP())
                .build();
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                // 这里 type 为 null,所以从 data 中解析结束状态
                TencentCompletionResponse response = JSON.parseObject(data, TencentCompletionResponse.class);
                TencentCompletionResponse.Choice choice = response.getChoices().get(0);

                final boolean stopped = FINISH_REASON_STOP.equals(choice.getFinishReason());
                if (stopped) {
                    TencentCompletionResponse.Usage tencentUsage = response.getUsage();

                    // 封装额度信息
                    Usage usage = new Usage();
                    usage.setPromptTokens(tencentUsage.getPromptTokens());
                    usage.setCompletionTokens(tencentUsage.getCompletionTokens());
                    usage.setTotalTokens(tencentUsage.getTotalTokens());

                    // 封装结束
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setFinishReason("stop");

                    String content = choice.getDelta().getContent();

                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(CompletionRequest.Role.SYSTEM)
                            .content(content)
                            .build());
                    choices.add(chatChoice);

                    // 构建结果
                    CompletionResponse completionResponse = new CompletionResponse();
                    completionResponse.setChoices(choices);
                    completionResponse.setUsage(usage);
                    completionResponse.setCreated(System.currentTimeMillis());
                    // 返回数据
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                } else {
                    CompletionResponse completionResponse = new CompletionResponse();
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();

                    String content = choice.getDelta().getContent();

                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(CompletionRequest.Role.SYSTEM)
                            .content(content)
                            .build());
                    choices.add(chatChoice);
                    completionResponse.setChoices(choices);
                    completionResponse.setCreated(System.currentTimeMillis());
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }
}
