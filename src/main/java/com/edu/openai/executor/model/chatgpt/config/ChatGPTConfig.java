package com.edu.openai.executor.model.chatgpt.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * chatGPT的配置类
 * 主要配置：apihost apikey 访问的路径
 */

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTConfig {

    @Getter
    @Setter
    private String apiHost = "https://api.openai-proxy.com/";

    @Getter
    @Setter
    private String apiKey;

    @Getter
    private String v1_chat_completions = "v1/chat/completions";
    @Getter
    String v1_images_completions = "v1/images/generations";
}
