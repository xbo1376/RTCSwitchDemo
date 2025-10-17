package com.tencent.trtc.live.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Set;

/**
 * SharedPreferences 工具类
 * 封装常用操作，支持多种数据类型
 */
public class SPUtils {
    private static final String DEFAULT_SP_NAME = "default_sp";
    private static volatile SPUtils instance;
    private final SharedPreferences sp;

    // 私有构造方法
    private SPUtils(Context context) {
        this(context, DEFAULT_SP_NAME);
    }

    private SPUtils(Context context, String spName) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null!");
        }
        if (TextUtils.isEmpty(spName)) {
            spName = DEFAULT_SP_NAME;
        }
        sp = context.getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 获取默认SPUtils实例(默认SharedPreferences名称)
     */
    public static SPUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtils.class) {
                if (instance == null) {
                    instance = new SPUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取自定义SPUtils实例
     */
    public static SPUtils getInstance(Context context, String spName) {
        return new SPUtils(context, spName);
    }

    /**
     * 保存String类型数据
     */
    public void putString(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取String类型数据
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 获取String类型数据，带默认值
     */
    public String getString(String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getString(key, defaultValue);
    }

    /**
     * 保存int类型数据
     */
    public void putInt(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 获取int类型数据
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取int类型数据，带默认值
     */
    public int getInt(String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getInt(key, defaultValue);
    }

    /**
     * 保存long类型数据
     */
    public void putLong(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putLong(key, value).apply();
    }

    /**
     * 获取long类型数据
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * 获取long类型数据，带默认值
     */
    public long getLong(String key, long defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getLong(key, defaultValue);
    }

    /**
     * 保存float类型数据
     */
    public void putFloat(String key, float value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putFloat(key, value).apply();
    }

    /**
     * 获取float类型数据
     */
    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    /**
     * 获取float类型数据，带默认值
     */
    public float getFloat(String key, float defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getFloat(key, defaultValue);
    }

    /**
     * 保存boolean类型数据
     */
    public void putBoolean(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取boolean类型数据
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取boolean类型数据，带默认值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 保存String Set类型数据
     */
    public void putStringSet(String key, Set<String> value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().putStringSet(key, value).apply();
    }

    /**
     * 获取String Set类型数据
     */
    public Set<String> getStringSet(String key) {
        return getStringSet(key, null);
    }

    /**
     * 获取String Set类型数据，带默认值
     */
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        return sp.getStringSet(key, defaultValue);
    }

    /**
     * 移除指定key的数据
     */
    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        sp.edit().remove(key).apply();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        sp.edit().clear().apply();
    }

    /**
     * 检查是否包含某个key
     */
    public boolean contains(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return sp.contains(key);
    }
}