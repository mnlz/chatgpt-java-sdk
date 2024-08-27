package com.edu.openai.test.model;

import java.util.HashMap;
import java.util.Map;

public class MaxSubarraySumEqualsK {

    public static int[] maxSubarraySumEqualsK(int[] nums, int k) {
        int maxLength = 0;
        int endIndex = -1;
        int prefixSum = 0;
        Map<Integer, Integer> sumToIndex = new HashMap<>(); // 使用哈希表记录前缀和以及对应的索引
        sumToIndex.put(0, -1);

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];
            if (sumToIndex.containsKey(prefixSum - k)) {
                int length = i - sumToIndex.get(prefixSum - k);
                if (length > maxLength) {
                    maxLength = length;
                    endIndex = i;
                }
            }
            if (!sumToIndex.containsKey(prefixSum)) {
                sumToIndex.put(prefixSum, i);
            }
        }

        if (endIndex == -1) {
            return new int[0]; // 没有找到满足条件的子数组
        }

        int startIndex = endIndex - maxLength + 1;
        int[] maxSubarray = new int[maxLength];
        for (int i = 0; i < maxLength; i++) {
            maxSubarray[i] = nums[startIndex + i];
        }

        return maxSubarray;
    }

    public static void main(String[] args) {
        int[] nums = {1, -1, 5, 2, 3};
        int k = 6;
        int[] maxSubarray = maxSubarraySumEqualsK(nums, k);
        if (maxSubarray.length > 0) {
            System.out.print("[");
            for (int i = 0; i < maxSubarray.length; i++) {
                System.out.print(maxSubarray[i]);
                if (i < maxSubarray.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        } else {
            System.out.println("No subarray found.");
        }
    }
}

