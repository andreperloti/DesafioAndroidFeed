package br.com.developers.perloti.desafioandroidtechfit.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Date;
import java.util.List;

/**
 * Created by perloti on 08/05/18.
 */

public class JsonUtil {

    public static LinkedTreeMap fromString(String rawdata) {
        if (rawdata == null) return null;
        LinkedTreeMap result = null;
        try {
            result = (new Gson()).fromJson(rawdata, LinkedTreeMap.class);
        } catch (Exception e) {
            Log.e("Erro-JsonUtil", "fromString: " + e.getMessage());
        }
        return result;
    }

    public static String toString(LinkedTreeMap data) {
        return (new Gson()).toJson(data);
    }

    public static boolean isNull(LinkedTreeMap tree, String key) {
        return tree.get(key) == null;
    }

    public static boolean hasKey(LinkedTreeMap tree, String key) {
        if (tree == null) return false;
        return tree.containsKey(key);
    }

    public static boolean hasValue(LinkedTreeMap tree, String key) {
        return hasKey(tree, key) && !isNull(tree, key);
    }

    public static String getString(LinkedTreeMap tree, String key, String defaultValue) {
        String result = getString(tree, key);
        return result == null ? defaultValue : result;
    }

    public static String getString(LinkedTreeMap tree, String key) {
        if (!hasValue(tree, key)) return null;
        return tree.get(key).toString();
    }

    public static int getInt(LinkedTreeMap tree, String key, int defaultValue) {
        if (!hasValue(tree, key)) return defaultValue;
        return ((Double) Double.parseDouble(getString(tree, key))).intValue();
    }

    public static long getLong(LinkedTreeMap tree, String key, long defaultValue) {
        if (!hasValue(tree, key)) return defaultValue;
        return ((Double) Double.parseDouble(getString(tree, key))).longValue();
    }

    public static float getFloat(LinkedTreeMap tree, String key, float defaultValue) {
        if (!hasValue(tree, key)) return defaultValue;
        return ((Double) Double.parseDouble(getString(tree, key))).floatValue();
    }

    public static boolean getBoolean(LinkedTreeMap tree, String key, boolean defaultValue) {
        if (!hasValue(tree, key)) return defaultValue;
        return (Boolean.parseBoolean(getString(tree, key)));
    }

    public static List<LinkedTreeMap> getList(LinkedTreeMap tree, String key) {
        if (!hasValue(tree, key)) return null;
        return (List<LinkedTreeMap>) tree.get(key);
    }


    public static List<String> getStringList(LinkedTreeMap tree, String key) {
        if (!hasValue(tree, key)) return null;
        return (List<String>) tree.get(key);
    }


    public static LinkedTreeMap getObject(LinkedTreeMap tree, String key) {
        if (!hasValue(tree, key)) return null;
        LinkedTreeMap result;
        try {
            result = (LinkedTreeMap) tree.get(key);
        } catch (ClassCastException e) {
            return null;
        }
        return result;
    }

    public static Date getDate(LinkedTreeMap tree, String key) {
        if (!hasValue(tree, key)) return null;
        return DateUtil.stringToDate(tree.get(key).toString());
    }


}