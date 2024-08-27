package com.edu.openai.executor.model.tencent.valobj;

import com.edu.openai.executor.parameter.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TencentMessage implements Serializable {

    //角色
    @JsonProperty("Role")
    private String role;

    //内容
    @JsonProperty("content")
    private String content;

    //提供一个 Message 的转换方法 将 Message 转化为 TencentMessage
    public static TencentMessage of(Message source){
        TencentMessage tencentMessage = new TencentMessage();
        tencentMessage.setRole(source.getRole());
        tencentMessage.setContent(source.getContent());
        return tencentMessage;

    }
}
