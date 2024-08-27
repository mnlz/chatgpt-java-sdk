package com.edu.openai.executor.model.tencent.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TencentConfig {
    @Builder.Default
    private String apiHost = "https://hunyuan.tencentcloudapi.com";

    /**
     *
     * SecretId:AKIDa6UlrqcFh3vb5fBYiicO1TnuoT5Xs36z
     * SecretKey:ofcTOOOMTadaWKLF0c6Vr4B9yjeYO9lV
     */
    private String secretId;
    /**
     * SecretKey
     */
    private String secretKey;

    @Builder.Default
    private String region = "ap-guangzhou";

    /**
     * ApiVersion
     */
    @Builder.Default
    private String apiVersion = "2023-09-01";

}
