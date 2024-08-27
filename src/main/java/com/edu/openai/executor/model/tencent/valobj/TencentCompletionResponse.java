package com.edu.openai.executor.model.tencent.valobj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TencentCompletionResponse implements Serializable {

    //免责声明
    @JsonProperty("Note")
    private String note;

    /**
     * 回复内容
     */
    @JsonProperty("Choices")
    private List<Choice> choices;
    @JsonProperty("Created")
    private Integer created;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Usage")
    private Usage usage;

    @Data
    public static class Choice implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 结束原因
         */
        @JsonProperty("FinishReason")
        private String finishReason;
        /**
         * Delta
         */
        @JsonProperty("Delta")
        private TencentMessage delta;
    }

    @Data
    public static class Usage implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 提示令牌
         */
        @JsonProperty("PromptTokens")
        private long promptTokens;
        /**
         * 完成令牌
         */
        @JsonProperty("CompletionTokens")
        private long completionTokens;
        /**
         * 总量令牌
         */
        @JsonProperty("TotalTokens")
        private long totalTokens;
    }
}
