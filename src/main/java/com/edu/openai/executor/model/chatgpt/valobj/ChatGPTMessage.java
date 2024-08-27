package com.edu.openai.executor.model.chatgpt.valobj;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 定义信息
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGPTMessage implements Serializable {

    private String role;
    private String content;
    private String name;

    public ChatGPTMessage() {
    }

    private ChatGPTMessage(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 建造者模式
     */
    public static final class Builder {

        private String role;
        private String content;
        private String name;

        public Builder() {
        }

        public Builder role(ChatGPTCompletionRequest.Role role) {
            this.role = role.getCode();
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ChatGPTMessage build() {
            return new ChatGPTMessage(this);
        }
    }

}
