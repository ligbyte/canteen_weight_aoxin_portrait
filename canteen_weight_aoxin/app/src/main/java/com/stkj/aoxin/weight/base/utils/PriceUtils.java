package com.stkj.aoxin.weight.base.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PriceUtils {

    /**
     * 格式化价格（保留两位小数，小数无0时补位）
     */
    public static String formatPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return "0.00";
        }
        String result = price;
        try {
            double parseDouble = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(parseDouble);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化价格（保留两位小数，小数无0时补位）
     */
    public static String formatPrice(double price) {
        try {
            BigDecimal bd = new BigDecimal(String.valueOf(price));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(price);
    }

    /**
     * 将小数四舍五入保留整数
     * @param number 需要处理的小数
     * @return 四舍五入后的整数字符串
     */
    public static long formatToInt(double number) {
        try {
            BigDecimal bd = new BigDecimal(String.valueOf(number));
            bd = bd.setScale(0, RoundingMode.HALF_UP);
            return bd.longValueExact();  // 转换为int，超出范围时抛出异常
        } catch (Exception e) {
            e.printStackTrace();
            // 可以根据需要返回默认值或抛出自定义异常
            return (int) Math.round(number);  // 使用Math.round作为备用方案
        }
    }


    /**
     * 格式化价格（保留两位小数，小数无0时不补位）
     */
    public static String formatPrice2(String price) {
        if (TextUtils.isEmpty(price)) {
            return "0.00";
        }
        String result = price;
        try {
            double parseDouble = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("0.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(parseDouble);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化价格（保留两位小数，小数无0时不补位）
     */
    public static String formatPrice2(Double price) {
        String result = String.valueOf(price);
        try {
            DecimalFormat df = new DecimalFormat("0.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            result = df.format(price);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String moJiaoPrice(String price) {
        String result = price;
        try {
            int indexOf = price.indexOf(".");
            if (indexOf != -1) {
                result = price.substring(0, indexOf) + ".00";
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String moFenPrice(String price) {
        String result = price;
        try {
            int indexOf = price.indexOf(".");
            if (indexOf != -1 && (indexOf + 2) < price.length()) {
                result = price.substring(0, indexOf + 2) + "0";
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double parsePrice(String price) {
        double result = 0;
        try {
            result = Double.parseDouble(price);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

}
