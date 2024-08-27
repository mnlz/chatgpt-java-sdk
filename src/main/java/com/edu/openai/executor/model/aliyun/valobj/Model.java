package com.edu.openai.executor.model.aliyun.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通义千问对话模型
 *
 * @author Vanffer
 */
@Getter
@AllArgsConstructor
public enum Model {
    QWEN_TURBO("qwen-turbo", "通义千问超大规模语言模型，支持中文英文等不同语言输入"),
    QWEN_PLUS("qwen-plus", "通义千问超大规模语言模型增强版，支持中文英文等不同语言输入"),
    QWEN_MAX("qwen-max", "通义千问2.1千亿级别超大规模语言模型，支持中文英文等不同语言输入"),
    ;

    private final String code;
    private final String info;

}
