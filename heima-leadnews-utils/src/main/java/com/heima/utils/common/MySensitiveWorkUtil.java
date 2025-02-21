package com.heima.utils.common;

import java.util.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 13:49
 */
public class MySensitiveWorkUtil {

    // 敏感词字典
    private static Map<String, Object> dictionary = new HashMap<String, Object>();

    public static void initDictionary(Collection<String> dictionaryKeys) {
        // 参数校验
        if (dictionaryKeys == null || dictionaryKeys.isEmpty()) {
            throw new IllegalArgumentException("dictionaryKeys is null or empty");
        }

        Map<String, Object> map = new HashMap<>(dictionaryKeys.size());

        // 遍历字典
        for (String key : dictionaryKeys) {
            Map<String, Object> curMap = map;
            for (int i = 0; i < key.length(); i++) {
                String word = String.valueOf(key.charAt(i));
                Map<String, Object> wordMap = (Map<String, Object>) curMap.get(word);
                if (wordMap == null) {
                    wordMap = new HashMap<>(2);
                    wordMap.put("isEnd", false);
                    curMap.put(word, wordMap);
                }
                curMap = (Map<String, Object>) curMap.get(word);
                if (i == key.length() - 1) {
                    wordMap.put("isEnd", true);
                }
            }
        }

        dictionary = map;
    }

    public static Map<String, Integer> matchDictionary(String textContent) {
        // 参数校验
        if (dictionary == null || dictionary.isEmpty()) {
            throw new IllegalArgumentException("dictionary is null or empty");
        }

        // 初始化参数
        Map<String, Integer> resultMap = new HashMap<>();
        Map<String, Object> curMap = dictionary;
        Integer wordLength = 0;
        Boolean flag = false;

        // 遍历文本
        for (int i = 0; i < textContent.length(); i++) {
            // 从当前index开始匹配一个敏感词
            for (int j = i; j < textContent.length(); j++) {
                // 当前词
                String word = String.valueOf(textContent.charAt(j));
                // 当前层
                Map<String, Object> wordMap = (Map<String, Object>) curMap.get(word);
                if (wordMap == null) {
                    // 匹配到敏感词后跳到该词后，重新匹配
                    if (flag) {
                        i += wordLength - 1;
                    }
                    // 重置参数
                    curMap = dictionary;
                    flag = false;
                    wordLength = 0;
                    break;
                } else {
                    // 判断匹配敏感词是否结束
                    if ((boolean) wordMap.get("isEnd")) {
                        // 截取敏感词
                        String sensitiveWord = textContent.substring(j - wordLength, j + 1);
                        // 存入结果map
                        if (resultMap.containsKey(sensitiveWord)) {
                            resultMap.put(sensitiveWord, resultMap.get(sensitiveWord) + 1);
                        } else {
                            resultMap.put(sensitiveWord, 1);
                        }
                        // 修改匹配状态
                        flag = true;
                    } else {
                        // 敏感词长度
                        wordLength++;
                        // 跳到下一层
                        curMap = wordMap;
                    }
                }
            }
        }

        return resultMap;

    }

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("冰毒");
        list.add("大麻");
        list.add("海洛因");
        list.add("法轮");
        list.add("法轮功");
        list.add("邪教");
        initDictionary(list);
        System.out.println(dictionary);

        String text = "拒绝毒品，拒绝冰毒，大麻，海洛因，更要拒绝法轮功，法轮功是邪教！";
        Map<String, Integer> matched = matchDictionary(text);
        System.out.println(matched);
    }

}
