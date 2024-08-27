package com.edu.openai.executor.model.tencent.utils;

import com.edu.openai.executor.model.tencent.config.TencentConfig;
import com.edu.openai.executor.model.tencent.valobj.CanonicalRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SecurityUtils {
    public static final String DEFAULT_ALGORITHM = "TC3-HMAC-SHA256";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(hexCode[(b >> 4) & 0xF]);
            sb.append(hexCode[(b & 0xF)]);
        }
        return sb.toString();
    }

    public static String sha256Hex(String str) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
        return printHexBinary(hash).toLowerCase();
    }

    public static byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String hmac256Hex(byte[] key, String msg) throws Exception {
        byte[] bytes = hmac256(key, msg);
        return printHexBinary(bytes).toLowerCase();
    }

    public static String getAuthorization(String requestPayload, String timestamp, TencentConfig config,String action) {
        try {
            URL url = new URL(config.getApiHost());
            String secretId = config.getSecretId();
            String secretKey = config.getSecretKey();

            Map<String, String> canonicalHeaders = new HashMap<>();
            canonicalHeaders.put("Content-Type", "application/json; charset=utf-8");
            canonicalHeaders.put("host", url.getHost());
            canonicalHeaders.put("x-tc-action", action.toLowerCase());
            String hashedRequestPayload = sha256Hex(requestPayload);

            // Service 一般是域名开头的
            String service = url.getHost().split("\\.")[0];

            // 1. 拼接规范请求字符串
            String canonicalRequest = CanonicalRequest.builder()
                    .httpRequestMethod("POST")
                    .canonicalURI("/")
                    .canonicalQueryString("")
                    .canonicalHeaders(canonicalHeaders)
                    .signedHeaders(canonicalHeaders.keySet())
                    .hashedRequestPayload(hashedRequestPayload)
                    .build()
                    .toString();
            // 2. 拼接待签名字符串

            // 这里必须使用 UTC 时间，文档有说明
            DEFAULT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = DEFAULT_DATE_FORMAT.format(new Date(Long.parseLong(timestamp + "000")));
            String credentialScope = date + "/" + service + "/" + "tc3_request";
            String hashedCanonicalRequest = SecurityUtils.sha256Hex(canonicalRequest);
            String stringToSign = DEFAULT_ALGORITHM + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

            // 3. 计算签名
            byte[] secretDate = SecurityUtils.hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
            byte[] secretService = SecurityUtils.hmac256(secretDate, service);
            byte[] secretSigning = SecurityUtils.hmac256(secretService, "tc3_request");
            String signature = DatatypeConverter.printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

            // 4. 拼接 Authentication
            String signedHeaders = CanonicalRequest.formatSignedHeaders(canonicalHeaders.keySet());
            return DEFAULT_ALGORITHM + " " + "Credential=" + secretId + "/" + credentialScope + ", "
                    + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failure to get authentication headers", e);
        }
    }
}
