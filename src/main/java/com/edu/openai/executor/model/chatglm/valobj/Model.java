package com.edu.openai.executor.model.chatglm.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Model {
    CHATGLM_4("glm-4", "根据输入的自然语言指令完成多种语言类任务"),
    CHATGLM_4v("glm-4v", "输入的自然语言指令和图像信息完成任务"),
    CHATGLM_3_TURBO("glm-3-turbo", "标准版模型，适用兼顾效果和成本的场景"),
    CHATGLM_STD("cogview-3", "根据用户的文字描述生成图像"),
    CHARACTERGLM("charglm-3", "支持基于人设的角色扮演、超长多轮的记忆、千人千面的角色对话，广泛应用于情感陪伴等"),

    ;
    private final String code;
    private final String info;
}
