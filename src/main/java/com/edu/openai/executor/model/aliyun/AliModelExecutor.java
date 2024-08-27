package com.edu.openai.executor.model.aliyun;

import com.alibaba.fastjson.JSON;
import com.edu.openai.executor.Executor;
import com.edu.openai.executor.model.aliyun.config.AliModelConfig;
import com.edu.openai.executor.model.aliyun.valobj.AliMessage;
import com.edu.openai.executor.model.aliyun.valobj.AliModelCompletionRequest;
import com.edu.openai.executor.model.aliyun.valobj.AliModelCompletionResponse;
import com.edu.openai.executor.model.aliyun.valobj.FinishReason;
import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
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

@Slf4j
public class AliModelExecutor implements Executor, ParameterHandler<AliModelCompletionRequest>, ResultHandler {

    private final EventSource.Factory factory;
    private final AliModelConfig aliModelConfig;

    public AliModelExecutor(Configuration configuration) {
        this.aliModelConfig = configuration.getAliModelConfig();
        this.factory = configuration.createRequestFactory();
    }


    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        return completions(null,null,completionRequest,eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {

        //1.转换参数
        AliModelCompletionRequest aliModelCompletionRequest = getParameterObject(completionRequest);

        String apiHost = apiHostByUser == null ? aliModelConfig.getApiHost():apiHostByUser;
        String apiKey = apiKeyByUser == null ? aliModelConfig.getApiKey():apiKeyByUser;
        //构建请求
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type","application/json")
                .header("X-DashScope-SSE", "enable")
                .url(apiHost.concat(aliModelConfig.getV1_completions()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), JSON.toJSONString(aliModelCompletionRequest)))
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
    public AliModelCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        //参数转换 completionRequest-> aliModelCompletionRequest
        AliModelCompletionRequest aliModelCompletionRequest = new AliModelCompletionRequest();
        AliModelCompletionRequest.Input input = new AliModelCompletionRequest.Input();
        List<Message> requestMessages = completionRequest.getMessages();
        List<AliMessage> aliMessageList = new ArrayList<>();
        for (Message requestMessage : requestMessages) {
            aliMessageList.add(AliMessage.builder()
                    .content(requestMessage.getContent())
                    .role(requestMessage.getRole())
                    .build());

        }
        input.setMessages(aliMessageList);
        aliModelCompletionRequest.setInput(input);
        aliModelCompletionRequest.setParameters(AliModelCompletionRequest.Parameters.builder().incrementalOutput(true).build());
        log.info("aliModelCompletionRequest:{}",JSON.toJSONString(aliModelCompletionRequest));
        return aliModelCompletionRequest;
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        //{"output":{"finish_reason":"null","text":"您好"},"usage":{"total_tokens":5,"input_tokens":4,"output_tokens":1},"request_id":"31d66f17-22fe-9783-9e9c-03315d0932fc"}
        //{"output":{"finish_reason":"stop","text":""},"usage":{"total_tokens":117,"input_tokens":4,"output_tokens":113},"request_id":"31d66f17-22fe-9783-9e9c-03315d0932fc"}

        return new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                // 对响应的结果进行解析，统一格式
                AliModelCompletionResponse response = JSON.parseObject(data, AliModelCompletionResponse.class);
                if (FinishReason.CONTINUE.getCode().equals(response.getOutput().getFinish_reason())) {
                    CompletionResponse completionResponse = new CompletionResponse();
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setDelta(Message.builder()
                            .role(CompletionRequest.Role.SYSTEM)
                            .name("")
                            .content(response.getOutput().getText())
                            .build());
                    choices.add(chatChoice);
                    completionResponse.setChoices(choices);
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                } else if (FinishReason.STOP.getCode().equals(response.getOutput().getFinish_reason())) {
                    AliModelCompletionResponse.Usage aliUsage = response.getUsage();
                    Usage usage = new Usage();
                    usage.setPromptTokens(aliUsage.getInput_tokens());
                    usage.setCompletionTokens(aliUsage.getOutput_tokens());
                    usage.setTotalTokens(aliUsage.getTotal_tokens());
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setFinishReason("stop");
                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(CompletionRequest.Role.SYSTEM)
                            .content(response.getOutput().getText())
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
                    onClosed(eventSource);
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onFailure(EventSource eventSource, @javax.annotation.Nullable Throwable t, @javax.annotation.Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }

        };
    }
}
