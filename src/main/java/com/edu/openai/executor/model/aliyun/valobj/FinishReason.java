package com.edu.openai.executor.model.aliyun.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinishReason {
    STOP("stop", "回答结束时的结束标识"),
    CONTINUE("null", "生成过程中"),
    Length("length", "token长度超出限制");

    private final String code;
    private final String info;
}
