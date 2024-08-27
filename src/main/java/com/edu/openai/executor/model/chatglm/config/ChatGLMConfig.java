package com.edu.openai.executor.model.chatglm.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMConfig {

    // api-key cf2f0b4d0e495be3e6c30698dc26b558.WZpFF6Li0xipmUKo
    // chat-模型 https://open.bigmodel.cn/api/paas/v4/chat/completions
    // 绘画-模型  https://open.bigmodel.cn/api/paas/v4/images/generations
    // 拟人-模型  https://open.bigmodel.cn/api/paas/v3/model-api/charglm-3/sse-invoke

    // 智普Ai ChatGlM 请求地址
    @Getter
    @Setter
    private String apiHost = "https://open.bigmodel.cn/";

    @Getter
    private String chat_v4_completions = "api/paas/v4/chat/completions";

    @Getter
    private String images_v4_completions = "api/paas/v4/images/generations";

    // 智普Ai https://open.bigmodel.cn/usercenter/apikeys - apiSecretKey = {apiKey}.{apiSecret}
    private String apiSecretKey;

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
        if (StringUtils.isBlank(apiSecretKey)) {
            log.warn("not apiSecretKey set");
        } else {
            String[] arrStr = apiSecretKey.split("\\.");
            if (arrStr.length != 2) {
                throw new RuntimeException("invalid apiSecretKey");
            }
            this.apiKey = arrStr[0];
            this.apiSecret = arrStr[1];
        }
    }
    @Getter
    private String apiKey;
    @Getter
    private String apiSecret;

}
