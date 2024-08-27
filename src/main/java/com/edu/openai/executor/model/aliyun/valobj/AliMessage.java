package com.edu.openai.executor.model.aliyun.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通义千问 对话消息
 *
 * @author zzw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliMessage {

    /**
     * 消息的角色
     */
    private String role;

    /**
     * 对话内容
     */
    private String content;
}