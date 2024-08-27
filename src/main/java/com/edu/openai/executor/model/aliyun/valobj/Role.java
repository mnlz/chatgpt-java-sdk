package com.edu.openai.executor.model.aliyun.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通义千问 对话角色
 */
@Getter
@AllArgsConstructor
public enum Role {
    system("system"),
    user("user"),
    assistant("assistant"),
    ;
    private final String code;
}

