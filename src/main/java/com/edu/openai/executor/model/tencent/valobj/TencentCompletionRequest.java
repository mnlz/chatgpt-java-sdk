package com.edu.openai.executor.model.tencent.valobj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 腾讯混元请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TencentCompletionRequest {

    /**
     * 聊天上下文信息。
     * 说明：
     * 1.长度最多为40, 按对话时间从旧到新在数组中排列。
     * 2.Message的Role当前可选值：user、assistant，其中，user和assistant需要交替出现(一问一答)，最后一个为user提问, 且Content不能为空。
     * 3.Messages中Content总长度不超过16000 token，超过则会截断最前面的内容，只保留尾部内容。建议不超过4000 token。
     */
    @JsonProperty("Messages")
    private List<TencentMessage> messages;

    /**
     * 说明：
     * 1.影响输出文本的多样性，取值越大，生成文本的多样性越强。
     * 2.默认1.0，取值区间为[0.0, 1.0]。
     * 3.非必要不建议使用, 不合理的取值会影响效果。
     */
    @JsonProperty("TopP")
    private Double topP;

    /**
     * Temperature
     * 说明：
     * 1.较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定。
     * 2.默认1.0，取值区间为[0.0，2.0]。
     * 3.非必要不建议使用,不合理的取值会影响效果。
     */
    @JsonProperty("Temperature")
    private Double temperature;
}
