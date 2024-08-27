package com.edu.openai.executor.model.chatglm.valobj;


import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMCompletionRequest {

    private  String model = Model.CHATGLM_4.getCode();
    /**
     * 请求ID
     */
    @JsonProperty("request_id")
    private String requestId = String.format("zzw-%d", System.currentTimeMillis());
    /**
     * 控制温度【随机性】
     */
    private double temperature = 0.9d;
    /**
     * 多样性控制；
     */
    @JsonProperty("top_p")
    private double topP = 0.7d;


    /** 问题描述 */
    private List<ChatGLMMessage> messages;;

    /**
     * 开启流式调用
     */
    private boolean stream = false;
    /**
     * sseformat, 用于兼容解决sse增量模式okhttpsse截取data:后面空格问题, [data: hello]。只在增量模式下使用sseFormat。
     */




    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("model", model);
        paramsMap.put("request_id", requestId);
        paramsMap.put("messages", messages);
        paramsMap.put("stream", stream);
        paramsMap.put("temperature", temperature);
        paramsMap.put("top_p", topP);
        try {
            return new ObjectMapper().writeValueAsString(paramsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
