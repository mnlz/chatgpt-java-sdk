package com.edu.openai.executor.model.chatgpt.valobj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片响应
 *
 * @author 小傅哥，微信：fustack
 */
@Data
public class ChatGPTImageResponse implements Serializable {

    private static final long serialVersionUID = 7794686357934848547L;

    /**
     * 条目数据
     */
    private List<ChatGPTItem> data;
    /**
     * 创建时间
     */
    private long created;
}
