package com.edu.openai.executor.model.tencent.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 腾讯云签名方法 V3
 * <a href="https://cloud.tencent.com/document/product/1729/101843">签名方法V3</a>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanonicalRequest {

    /**
     * HTTPRequestMethod
     * HTTP 请求方法（GET、POST ）
     */
    private String httpRequestMethod;
    /**
     * CanonicalURI
     * URI 参数，API 3.0 固定为正斜杠（/）
     */
    private String canonicalURI;
    /**
     * CanonicalQueryString
     * 发起 HTTP 请求 URL 中的查询字符串，对于 POST 请求，固定为空字符串""，对于 GET 请求，则为 URL 中问号（?）后面的字符串内容，例如：Limit=10&Offset=0。
     * 注意：CanonicalQueryString 需要参考 RFC3986 进行 URLEncode 编码（特殊字符编码后需大写字母），字符集 UTF-8。推荐使用编程语言标准库进行编码。
     */
    private String canonicalQueryString;
    /**
     * CanonicalHeaders
     * 参与签名的头部信息，至少包含 host 和 content-type 两个头部，也可加入其他头部参与签名以提高自身请求的唯一性和安全性，此示例额外增加了接口名头部。
     * 拼接规则：
     * 1. 头部 key 和 value 统一转成小写，并去掉首尾空格，按照 key:value\n 格式拼接；
     * 2. 多个头部，按照头部 key（小写）的 ASCII 升序进行拼接。
     * 此示例计算结果是 content-type:application/json; charset=utf-8\nhost:cvm.tencentcloudapi.com\nx-tc-action:describeinstances\n。
     * 注意：content-type 必须和实际发送的相符合，有些编程语言网络库即使未指定也会自动添加 charset 值，如果签名时和发送时不一致，服务器会返回签名校验失败。
     */
    private Map<String, String> canonicalHeaders;
    /**
     * SignedHeaders
     * 参与签名的头部信息，说明此次请求有哪些头部参与了签名，和 CanonicalHeaders 包含的头部内容是一一对应的。content-type 和 host 为必选头部。
     * 拼接规则：
     * 1. 头部 key 统一转成小写；
     * 2. 多个头部 key（小写）按照 ASCII 升序进行拼接，并且以分号（;）分隔。
     * 此示例为 content-type;host;x-tc-action
     */
    private Set<String> signedHeaders;
    /**
     * HashedRequestPayload
     * 请求正文（payload，即 body，此示例为 {"Limit": 1, "Filters": [{"Values": ["\u672a\u547d\u540d"], "Name": "instance-name"}]}）的哈希值，
     * 计算伪代码为 Lowercase(HexEncode(Hash.SHA256(RequestPayload)))，即对 HTTP 请求正文做 SHA256 哈希，然后十六进制编码，最后编码串转换成小写字母。
     * 对于 GET 请求，RequestPayload 固定为空字符串。此示例计算结果是 35e9c5b0e3ae67532d3c9f17ead6c90222632e5b1ff7f6e89887f1398934f064。
     */
    private String hashedRequestPayload;

    public static String formatCanonicalHeaders(Map<String, String> headers) {
        TreeMap<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedHeaders.putAll(headers);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
            String key = entry.getKey().toLowerCase().trim();
            String value = entry.getValue().toLowerCase().trim();
            stringBuilder.append(key).append(":").append(value).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String formatSignedHeaders(Set<String> headerskeySet) {
        StringBuilder sb = new StringBuilder();
        List<String> lowercaseKeys = new ArrayList<>(headerskeySet);
        lowercaseKeys.sort(String.CASE_INSENSITIVE_ORDER);
        for (String header : lowercaseKeys) {
            sb.append(header.toLowerCase()).append(";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.httpRequestMethod + "\n" +
                this.canonicalURI + "\n" +
                this.canonicalQueryString + "\n" +
                formatCanonicalHeaders(this.canonicalHeaders) + "\n" +
                formatSignedHeaders(this.signedHeaders) + "\n" +
                this.hashedRequestPayload;
    }
}

