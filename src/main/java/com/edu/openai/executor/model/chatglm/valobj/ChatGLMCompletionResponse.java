package com.edu.openai.executor.model.chatglm.valobj;

import com.edu.openai.executor.model.chatgpt.valobj.ChatChoice;
import com.edu.openai.executor.model.chatgpt.valobj.Usage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatGLMCompletionResponse {
    /** ID */
    private String id;
    /** 对象 */
    private String object;
    /** 模型 */
    private String model;
    /** 对话 */
    private List<ChatChoice> choices;
    /** 创建 */
    private long created;
    /** 耗材 */
    private Usage usage;



    @Data
    public static class Usage {
        private int completion_tokens;
        private int prompt_tokens;
        private int total_tokens;
    }

}
