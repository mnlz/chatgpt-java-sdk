package com.edu.openai.executor.model.chatglm.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ChatGLM 消息类型
 *
 * @author 小傅哥，微信：fustack
 */
@Getter
@AllArgsConstructor
public enum EventType {

    add("add", "增量"),
    finish("finish", "结束"),
    error("error", "错误"),
    interrupted("interrupted", "中断"),

    ;
    private final String code;
    private final String info;

}