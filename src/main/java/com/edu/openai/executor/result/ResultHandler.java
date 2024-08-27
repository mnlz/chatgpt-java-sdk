package com.edu.openai.executor.result;

import okhttp3.sse.EventSourceListener;

/**
 * 处理请求，eventSourceListener传入一个回调函数
 */
public interface ResultHandler {
    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);

}
