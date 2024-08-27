package com.edu.openai.executor.model.chatglm.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMMessage  implements Serializable {
    private String role;
    private String content;
}
