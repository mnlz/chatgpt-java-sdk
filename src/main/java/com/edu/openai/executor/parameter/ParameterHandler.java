package com.edu.openai.executor.parameter;

/**
 * 参数处理器 获取范型类型的参数
 * @param <T>
 */
public interface ParameterHandler<T>{
    T getParameterObject(CompletionRequest completionRequest);
}
