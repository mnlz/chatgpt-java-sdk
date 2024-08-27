package com.edu.openai.executor.parameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 对于图片请求的封装
 * curl https://api.openai.com/v1/images/generations \
 *   -H "Content-Type: application/json" \
 *   -H "Authorization: Bearer $OPENAI_API_KEY" \
 *   -d '{
 *     "model": "dall-e-3",
 *     "prompt": "A cute baby sea otter",
 *     "n": 1,
 *     "size": "1024x1024"
 *   }'
 */
@Slf4j
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest extends ImageEnum implements Serializable {

    /** 模型 */
    private String model = Model.DALL_E_3.code;

    /** 问题表述*/
    @NonNull
    private String prompt;

    /** 生成一张图片*/
    @Builder.Default
    private Integer n = 1;

    /** 默认生成的大小*/
    @Builder.Default
    private String size  = Size.size_256.getCode();

    @Getter
    @AllArgsConstructor
    public enum Model {
        DALL_E_2("dall-e-2"),
        DALL_E_3("dall-e-3"),
        STABLE_DIFFUSION_XL("Stable_Diffusion_XL"),
        ;
        private final String code;
    }

}
