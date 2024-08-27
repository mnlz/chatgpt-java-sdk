package com.edu.openai.executor.model.chatgpt.valobj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 条目
 *
 * @author 小傅哥，微信：fustack
 */
@Data
public class ChatGPTItem implements Serializable {

    private static final long serialVersionUID = 3244723712850679296L;

    private String url;
    //    @JsonProperty("b64_json")
//    private String b64Json;
    @JsonProperty("revised_prompt")
    private String revisedPrompt;
}
