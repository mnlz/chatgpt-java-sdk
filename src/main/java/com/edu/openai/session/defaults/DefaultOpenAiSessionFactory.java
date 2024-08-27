package com.edu.openai.session.defaults;

import com.edu.openai.executor.Executor;
import com.edu.openai.executor.interceptor.HTTPInterceptor;
import com.edu.openai.session.Configuration;
import com.edu.openai.session.OpenAiSession;
import com.edu.openai.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory {

    private final Configuration configuration;

    public DefaultOpenAiSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    //使用工厂模式进行Session的创建
    @Override
    public OpenAiSession openSession() {
        // 1. 日志配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // 2. 开启 Http 客户端
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new HTTPInterceptor(configuration))
                .connectTimeout(450, TimeUnit.SECONDS)
                .writeTimeout(450, TimeUnit.SECONDS)
                .readTimeout(450, TimeUnit.SECONDS)
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 21284)))
                .build();
        configuration.setOkHttpClient(okHttpClient);

        // 3. 创建执行器【模型 -> 映射执行器】
        HashMap<String, Executor> executorGroup = configuration.newExecutorGroup();
        System.out.println(executorGroup.size() + " :executors");

        return new DefaultOpenAiSession(configuration, executorGroup);
    }

}
