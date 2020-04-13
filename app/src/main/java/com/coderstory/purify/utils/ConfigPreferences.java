package com.coderstory.purify.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import static com.coderstory.purify.config.Misc.BasePath;
import static com.coderstory.purify.utils.FileUtils.fileExists;
import static com.coderstory.purify.utils.FileUtils.readFile;
import static com.coderstory.purify.utils.FileUtils.writeFile;

public class ConfigPreferences {
    /*文件路径*/
    private final static String DEFAULT_FILENAME = BasePath + "/config.ini";
    private static JSONObject config;

    private ConfigPreferences() {
        if (!fileExists(DEFAULT_FILENAME)) {
            config = new JSONObject();
            String jsonString = JSON.toJSONString(config);
            writeFile(DEFAULT_FILENAME, jsonString);
        } else {
            reload();
        }
    }

    public synchronized static ConfigPreferences getInstance() {
        reload();
        return ConfigPreferencesHolder.configPreferences;
    }

    public static synchronized void reload() {
        String jsonString = readFile(DEFAULT_FILENAME);

        if (!jsonString.equals("")) {
            config = JSON.parseObject(jsonString, JSONObject.class);
        } else {
            config = new JSONObject();
        }
    }

    /**
     * 写入配置文件
     *
     * @param key   写入的key
     * @param value 写入key的值
     */
    public void saveConfig(String key, Object value) {
        config.remove(key);
        config.put(key, value);
        String result = config.toString();
        writeFile(DEFAULT_FILENAME, result);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return config.containsKey(key) ? (boolean) config.get(key) : defaultValue;
    }

    public String getString(String key, String defaultValue) {
        return config.containsKey(key) ? (String) config.get(key) : defaultValue;
    }

    private static class ConfigPreferencesHolder {
        private final static ConfigPreferences configPreferences = new ConfigPreferences();
    }

}