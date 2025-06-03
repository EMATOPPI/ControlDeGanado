package com.example.ControlDeGanado.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {
    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = success(message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> error(String message, Object details) {
        Map<String, Object> response = error(message);
        response.put("details", details);
        return response;
    }
}