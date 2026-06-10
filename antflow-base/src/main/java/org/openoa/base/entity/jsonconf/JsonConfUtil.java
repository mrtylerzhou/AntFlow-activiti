package org.openoa.base.entity.jsonconf;

import com.alibaba.fastjson2.JSON;

/**
 * Utility for serializing/deserializing BPMN configuration JSON.
 */
public class JsonConfUtil {

    private JsonConfUtil() {
    }

    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    public static BpmnNodeConfigJson parseNodeConfig(String json) {
        return parseObject(json, BpmnNodeConfigJson.class);
    }

    public static BpmnConfConfigJson parseConfConfig(String json) {
        return parseObject(json, BpmnConfConfigJson.class);
    }

    public static String toNodeConfigJson(BpmnNodeConfigJson config) {
        return toJsonString(config);
    }

    public static String toConfConfigJson(BpmnConfConfigJson config) {
        return toJsonString(config);
    }

    public static VariableConfigJson parseVariableConfig(String json) {
        return parseObject(json, VariableConfigJson.class);
    }

    public static String toVariableConfigJson(VariableConfigJson config) {
        return toJsonString(config);
    }
}
