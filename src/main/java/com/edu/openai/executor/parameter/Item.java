package com.edu.openai.executor.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//封装请求响应的对象
@Data
public class Item {
    private String url;
    @JsonProperty("b64_json")
    private String b64Json;
}
