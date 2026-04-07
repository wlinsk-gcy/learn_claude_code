package com.wlinsk.learn_claude_code.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author wlinsk
 * @date 2026/4/6
 */
public class JsonUtil {
    private static final Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
