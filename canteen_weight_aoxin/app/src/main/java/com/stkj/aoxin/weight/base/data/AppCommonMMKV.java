package com.stkj.aoxin.weight.base.data;

import com.tencent.mmkv.MMKV;

public class AppCommonMMKV {

    public static final String MMKV_NAME = "app_common";

    public static final String KEY_HAS_ADD_SHUT_CUT = "has_add_shut_cut";


    public static void putHasAddShutCut(boolean hasAssShutCut) {
        MMKV settingMMKV = getSettingMMKV();
        settingMMKV.putBoolean(KEY_HAS_ADD_SHUT_CUT, hasAssShutCut);
    }

    public static boolean getHasAddShutCut() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeBool(KEY_HAS_ADD_SHUT_CUT, false);
    }


    public static void putOrderData(String key,String value) {
        MMKV settingMMKV = getSettingMMKV();
        settingMMKV.putString(key, value);
    }

    public static String getOrderData(String key) {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeString(key);
    }

    public static void removeOrderData(String key) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.remove(key);
    }


    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }

}
