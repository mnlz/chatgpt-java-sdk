package com.edu.openai.executor.model.tencent.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum Model {
    CHAT_STD("ChatStd", "适用于对知识量、推理能力、创造力要求较高的场景"),
    CHAT_PRO("ChatPro", "适用于对知识量、推理能力、创造力要求较高的场景");
    private  String code;
    private  String info;
}
