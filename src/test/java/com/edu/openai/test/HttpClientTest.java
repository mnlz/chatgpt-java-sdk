// package com.edu.openai.test;
//
// import com.edu.openai.IOpenAiApi;
// import com.edu.openai.common.Constants;
// import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionRequest;
// import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionResponse;
// import com.edu.openai.executor.parameter.Message;
// import cn.hutool.http.ContentType;
// import cn.hutool.http.Header;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import io.reactivex.Single;
// import lombok.extern.slf4j.Slf4j;
// import okhttp3.*;
// import okhttp3.logging.HttpLoggingInterceptor;
// import okhttp3.sse.EventSource;
// import okhttp3.sse.EventSourceListener;
// import okhttp3.sse.EventSources;
// import org.junit.Test;
// import retrofit2.Retrofit;
// import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
// import retrofit2.converter.jackson.JacksonConverterFactory;
//
// import java.util.Collections;
// import java.util.concurrent.CountDownLatch;
//
// /**
//  * @author 小傅哥，微信：fustack
//  * @description
//  * @github https://github.com/fuzhengwei
//  * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
//  */
// @Slf4j
// public class HttpClientTest {
//
//     public static void main(String[] args) {
//
//     }
//
//     @Test
//     public  void test_client() {
//         HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//         httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//         OkHttpClient okHttpClient = new OkHttpClient
//                 .Builder()
//                 .addInterceptor(httpLoggingInterceptor)
//                 .addInterceptor(chain -> {
//                     Request original = chain.request();
//
//                     // 从请求中获取 token 参数，并将其添加到请求路径中
//                     HttpUrl url = original.url().newBuilder()
//                             .addQueryParameter("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4ZmciLCJleHAiOjE2ODMyNzIyMjAsImlhdCI6MTY4MzI2ODYyMCwianRpIjoiOTkwMmM4MjItNzI2MC00OGEwLWI0NDUtN2UwZGZhOGVhOGYwIiwidXNlcm5hbWUiOiJ4ZmcifQ.Om7SdWdiIevvaWdPn7D9PnWS-ZmgbNodYTh04Tfb124")
//                             .build();
//
//                     Request request = original.newBuilder()
//                             .url(url)
//                             .header(Header.AUTHORIZATION.getValue(), "Bearer " + "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")
//                             .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
//                             .method(original.method(), original.body())
//                             .build();
//                     return chain.proceed(request);
//                 })
//                 .build();
//
//         IOpenAiApi openAiApi = new Retrofit.Builder()
//                 .baseUrl("https://api.openai-proxy.com")
//                 .client(okHttpClient)
//                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                 .addConverterFactory(JacksonConverterFactory.create())
//                 .build().create(IOpenAiApi.class);
//
//         Message message = Message.builder().role(Constants.Role.USER).content("写一个java冒泡排序").build();
//         ChatGPTCompletionRequest chatCompletion = ChatGPTCompletionRequest
//                 .builder()
//                 .messages(Collections.singletonList(message))
//                 .model(ChatGPTCompletionRequest.Model.GPT_3_5_TURBO.getCode())
//                 .build();
//
//         Single<ChatGPTCompletionResponse> chatCompletionResponseSingle = openAiApi.completions(chatCompletion);
//         ChatGPTCompletionResponse chatCompletionResponse = chatCompletionResponseSingle.blockingGet();
//         chatCompletionResponse.getChoices().forEach(e -> {
//             System.out.println(e.getMessage());
//         });
//     }
//
//     @Test
//     public void test_stream() throws JsonProcessingException, InterruptedException {
//         HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//         httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//         OkHttpClient okHttpClient = new OkHttpClient
//                 .Builder()
//                 .addInterceptor(httpLoggingInterceptor)
//                 .addInterceptor(chain -> {
//                     Request original = chain.request();
//
//                     // 从请求中获取 token 参数，并将其添加到请求路径中
//                     HttpUrl url = original.url().newBuilder()
//                             .addQueryParameter("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4ZmciLCJleHAiOjE2ODMyNzIyMjAsImlhdCI6MTY4MzI2ODYyMCwianRpIjoiOTkwMmM4MjItNzI2MC00OGEwLWI0NDUtN2UwZGZhOGVhOGYwIiwidXNlcm5hbWUiOiJ4ZmcifQ.Om7SdWdiIevvaWdPn7D9PnWS-ZmgbNodYTh04Tfb124")
//                             .build();
//
//                     Request request = original.newBuilder()
//                             .url(url)
//                             .header(Header.AUTHORIZATION.getValue(), "Bearer " + "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")
//                             .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
//                             .method(original.method(), original.body())
//                             .build();
//                     return chain.proceed(request);
//                 })
//                 .build();
//         //创建请求体中的message
//         Message message = Message.builder().role(Constants.Role.USER).content("你是基于GPT几代的模型").build();
//         ChatGPTCompletionRequest chatCompletion = ChatGPTCompletionRequest
//                 .builder()
//                 .messages(Collections.singletonList(message))
//                 .stream(true)
//                 .model(ChatGPTCompletionRequest.Model.GPT_3_5_TURBO.getCode())
//                 .build();
//         //开启一个 EventSource 服务器向客户端单向通信的模型
//         EventSource.Factory factory = EventSources.createFactory(okHttpClient);
//         //构建请求体
//         String requestBody = new ObjectMapper().writeValueAsString(chatCompletion);
//         //创建request请求
//         Request request = new Request.Builder()
//                 .url("https://api.openai-proxy.com/v1/chat/completions")
//                 .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),requestBody))
//                 .build();
//         //客户端监听EventSource
//         EventSource eventSource = factory.newEventSource(request, new EventSourceListener() {
//             //打印监听的结果 数据全部存储在data中
//             @Override
//             public void onEvent(EventSource eventSource, String id, String type, String data) {
//                 log.info("测试结果：{}", data);
//
//             }
//         });
//         //主线程阻塞，需要持续的监听
//         new CountDownLatch(1).await();
//     }
//
// }
