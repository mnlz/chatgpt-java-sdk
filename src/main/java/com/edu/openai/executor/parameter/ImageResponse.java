package com.edu.openai.executor.parameter;

import lombok.Data;

import java.util.List;

/**
 * {
 *   "created": 1589478378,
 *   "data": [
 *     {
 *       "url": "https://..."
 *     },
 *     {
 *       "url": "https://..."
 *     }
 *   ]
 * }
 */
@Data
public class ImageResponse {
    private List<Item> data;
    private long created;
}
