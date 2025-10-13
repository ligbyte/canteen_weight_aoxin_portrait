package com.stkj.aoxin.weight.utils;

/**
 * 工具类：将数字转换为对应的中文次数描述
 * 映射规则：0→首次, 1→二次, 2→三次, 3→四次, 4→五次, 5→六次, 以此类推
 */
public class OccurrenceConverter {

    /**
     * 将数字转换为中文次数描述
     * @param n 输入的数字（非负整数）
     * @return 对应的中文次数字符串
     * @throws IllegalArgumentException 如果n为负数
     */
    public static String getOccurrence(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("输入必须为非负整数");
        }

        // 处理特殊情况：n=0时返回"首次"
        if (n == 0) {
            return "首次";
        }

        // 对于n>=1，将数字n+1转换为中文数字并添加"次"后缀
        return numberToChinese(n + 1) + "次";
    }

    /**
     * 将数字转换为中文数字字符串（支持0-999）
     * @param num 输入数字（1及以上）
     * @return 中文数字字符串
     */
    private static String numberToChinese(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("数字必须为非负");
        }
        if (num == 0) {
            return "零";
        }

        String[] digits = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

        // 处理0-9
        if (num < 10) {
            return digits[num];
        }

        // 处理10-19
        if (num < 20) {
            if (num == 10) {
                return "十";
            } else {
                return "十" + digits[num - 10];
            }
        }

        // 处理20-99
        if (num < 100) {
            int tens = num / 10;
            int units = num % 10;
            return digits[tens] + "十" + (units > 0 ? digits[units] : "");
        }

        // 处理100-999
        if (num < 1000) {
            int hundreds = num / 100;
            int remainder = num % 100;
            if (remainder == 0) {
                return digits[hundreds] + "百";
            } else if (remainder < 10) {
                return digits[hundreds] + "百零" + digits[remainder];
            } else {
                return digits[hundreds] + "百" + numberToChinese(remainder);
            }
        }

        // 对于1000及以上的数字，直接返回阿拉伯数字字符串（避免复杂转换）
        return String.valueOf(num);
    }

    // 测试用例
    public static void main(String[] args) {
        // 测试0-5的映射
        int[] testCases = {0, 1, 2, 3, 4, 5};
        for (int n : testCases) {
            System.out.println(n + " → " + getOccurrence(n));
        }

        // 测试扩展案例
        System.out.println("10 → " + getOccurrence(10)); // 应返回"十一次"
        System.out.println("15 → " + getOccurrence(15)); // 应返回"十六次"
    }
}
