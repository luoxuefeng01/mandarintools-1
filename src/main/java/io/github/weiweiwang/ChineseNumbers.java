package io.github.weiweiwang;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by wangweiwei01 on 17/4/1.
 */
public class ChineseNumbers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChineseNumbers.class);
    private static final String[] DIGITS = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static final Map<Character, Integer> DIGITS_MAP = new HashMap<>();
    private static final Pattern DIGITS_PATTERN;
    private static final Pattern ENGLISH_DECIMAL_PATTERN = Pattern.compile("([0-9]*)\\.([0-9]+)");
    private static final Pattern ENGLISH_FRACTION_PATTERN = Pattern.compile("([0-9]*)/([0-9]+)");

    private static final String[] BEFORE_WAN_DIGITS = {"十", "百", "千"};
    private static final Map<String, Integer> BEFORE_WAN_DIGITS_MAP = new HashMap<>();

    private static final String[] AFTER_WAN_DIGITS = {"", "萬", "億", "兆", "京"};
    private static final Map<String, Long> AFTER_WAN_DIGITS_MAP = new HashMap<>();

    private static final Map<String, String> TRADITIONAL_TO_SIMPLE_MAP = new HashMap<>();
    private static final Map<String, String> TRADITIONAL_TO_FORMAL_MAP = new HashMap<>();

    private static final Map<String, String> TRADITIONAL_TO_PINYIN_MAP = new HashMap<>();

    private static final String MINUS = "负";
    private static final String DECIMAL = "点";
    private static final String FRACTION = "分之";
//    public static final String ALTTWO = "兩";


    static {
        DIGITS_MAP.put('0', 0);
        DIGITS_MAP.put('1', 1);
        DIGITS_MAP.put('2', 2);
        DIGITS_MAP.put('3', 3);
        DIGITS_MAP.put('4', 4);
        DIGITS_MAP.put('5', 5);
        DIGITS_MAP.put('6', 6);
        DIGITS_MAP.put('7', 7);
        DIGITS_MAP.put('8', 8);
        DIGITS_MAP.put('9', 9);
        DIGITS_MAP.put('〇', 0);
        DIGITS_MAP.put('一', 1);
        DIGITS_MAP.put('七', 7);
        DIGITS_MAP.put('三', 3);
        DIGITS_MAP.put('两', 2);
        DIGITS_MAP.put('九', 9);
        DIGITS_MAP.put('二', 2);
        DIGITS_MAP.put('五', 5);
        DIGITS_MAP.put('伍', 5);
        DIGITS_MAP.put('兩', 2);
        DIGITS_MAP.put('八', 8);
        DIGITS_MAP.put('六', 6);
        DIGITS_MAP.put('叁', 3);
        DIGITS_MAP.put('參', 3);
        DIGITS_MAP.put('叄', 3);
        DIGITS_MAP.put('四', 4);
        DIGITS_MAP.put('壹', 1);
        DIGITS_MAP.put('捌', 8);
        DIGITS_MAP.put('柒', 7);
        DIGITS_MAP.put('玖', 9);
        DIGITS_MAP.put('肆', 4);
        DIGITS_MAP.put('貳', 2);
        DIGITS_MAP.put('贰', 2);
        DIGITS_MAP.put('陆', 6);
        DIGITS_MAP.put('陸', 6);
        DIGITS_MAP.put('零', 0);
        DIGITS_MAP.put('０', 0);
        DIGITS_MAP.put('１', 1);
        DIGITS_MAP.put('２', 2);
        DIGITS_MAP.put('３', 3);
        DIGITS_MAP.put('４', 4);
        DIGITS_MAP.put('５', 5);
        DIGITS_MAP.put('６', 6);
        DIGITS_MAP.put('７', 7);
        DIGITS_MAP.put('８', 8);
        DIGITS_MAP.put('９', 9);
        String join = DIGITS_MAP.keySet().stream().map(String::valueOf).collect(Collectors.joining(""));
        String pattern = String.format("^[%s]+$", join);
        DIGITS_PATTERN = Pattern.compile(pattern);

        BEFORE_WAN_DIGITS_MAP.put("仟", 1000);
        BEFORE_WAN_DIGITS_MAP.put("佰", 100);
        BEFORE_WAN_DIGITS_MAP.put("十", 10);
        BEFORE_WAN_DIGITS_MAP.put("千", 1000);
        BEFORE_WAN_DIGITS_MAP.put("拾", 10);
        BEFORE_WAN_DIGITS_MAP.put("百", 100);

        AFTER_WAN_DIGITS_MAP.put("万", 10000L);
        AFTER_WAN_DIGITS_MAP.put("京", 10000000000000000L);
        AFTER_WAN_DIGITS_MAP.put("亿", 100000000L);
        AFTER_WAN_DIGITS_MAP.put("億", 100000000L);
        AFTER_WAN_DIGITS_MAP.put("兆", 1000000000000L);
        AFTER_WAN_DIGITS_MAP.put("萬", 10000L);

        TRADITIONAL_TO_FORMAL_MAP.put("一", "壹");
        TRADITIONAL_TO_FORMAL_MAP.put("一", "壹");
        TRADITIONAL_TO_FORMAL_MAP.put("七", "柒");
        TRADITIONAL_TO_FORMAL_MAP.put("七", "柒");
        TRADITIONAL_TO_FORMAL_MAP.put("三", "參");
        TRADITIONAL_TO_FORMAL_MAP.put("三", "參");
        TRADITIONAL_TO_FORMAL_MAP.put("九", "玖");
        TRADITIONAL_TO_FORMAL_MAP.put("九", "玖");
        TRADITIONAL_TO_FORMAL_MAP.put("二", "貳");
        TRADITIONAL_TO_FORMAL_MAP.put("二", "貳");
        TRADITIONAL_TO_FORMAL_MAP.put("五", "伍");
        TRADITIONAL_TO_FORMAL_MAP.put("五", "伍");
        TRADITIONAL_TO_FORMAL_MAP.put("億", "億");
        TRADITIONAL_TO_FORMAL_MAP.put("億", "億");
        TRADITIONAL_TO_FORMAL_MAP.put("兆", "兆");
        TRADITIONAL_TO_FORMAL_MAP.put("兆", "兆");
        TRADITIONAL_TO_FORMAL_MAP.put("兩", "兩");
        TRADITIONAL_TO_FORMAL_MAP.put("兩", "兩");
        TRADITIONAL_TO_FORMAL_MAP.put("八", "捌");
        TRADITIONAL_TO_FORMAL_MAP.put("八", "捌");
        TRADITIONAL_TO_FORMAL_MAP.put("六", "陸");
        TRADITIONAL_TO_FORMAL_MAP.put("六", "陸");
        TRADITIONAL_TO_FORMAL_MAP.put("十", "拾");
        TRADITIONAL_TO_FORMAL_MAP.put("十", "拾");
        TRADITIONAL_TO_FORMAL_MAP.put("千", "仟");
        TRADITIONAL_TO_FORMAL_MAP.put("千", "仟");
        TRADITIONAL_TO_FORMAL_MAP.put("四", "肆");
        TRADITIONAL_TO_FORMAL_MAP.put("四", "肆");
        TRADITIONAL_TO_FORMAL_MAP.put("百", "佰");
        TRADITIONAL_TO_FORMAL_MAP.put("百", "佰");
        TRADITIONAL_TO_FORMAL_MAP.put("萬", "萬");
        TRADITIONAL_TO_FORMAL_MAP.put("萬", "萬");
        TRADITIONAL_TO_FORMAL_MAP.put("負", "負");
        TRADITIONAL_TO_FORMAL_MAP.put("負", "負");
        TRADITIONAL_TO_FORMAL_MAP.put("零", "零");
        TRADITIONAL_TO_FORMAL_MAP.put("零", "零");
        TRADITIONAL_TO_FORMAL_MAP.put("點", "點");
        TRADITIONAL_TO_FORMAL_MAP.put("點", "點");
        TRADITIONAL_TO_PINYIN_MAP.put("一", "yi1");
        TRADITIONAL_TO_PINYIN_MAP.put("七", "qi1");
        TRADITIONAL_TO_PINYIN_MAP.put("三", "san1");
        TRADITIONAL_TO_PINYIN_MAP.put("九", "jiu3");
        TRADITIONAL_TO_PINYIN_MAP.put("二", "er4");
        TRADITIONAL_TO_PINYIN_MAP.put("五", "wu3");
        TRADITIONAL_TO_PINYIN_MAP.put("億", "yi4");
        TRADITIONAL_TO_PINYIN_MAP.put("兆", "zhao4");
        TRADITIONAL_TO_PINYIN_MAP.put("兩", "liang3");
        TRADITIONAL_TO_PINYIN_MAP.put("八", "ba1");
        TRADITIONAL_TO_PINYIN_MAP.put("六", "liu4");
        TRADITIONAL_TO_PINYIN_MAP.put("十", "shi2");
        TRADITIONAL_TO_PINYIN_MAP.put("千", "qian1");
        TRADITIONAL_TO_PINYIN_MAP.put("四", "si4");
        TRADITIONAL_TO_PINYIN_MAP.put("百", "bai3");
        TRADITIONAL_TO_PINYIN_MAP.put("萬", "wan4");
        TRADITIONAL_TO_PINYIN_MAP.put("負", "fu4");
        TRADITIONAL_TO_PINYIN_MAP.put("零", "ling2");
        TRADITIONAL_TO_PINYIN_MAP.put("點", "dian3");
        TRADITIONAL_TO_SIMPLE_MAP.put("一", "一");
        TRADITIONAL_TO_SIMPLE_MAP.put("七", "七");
        TRADITIONAL_TO_SIMPLE_MAP.put("三", "三");
        TRADITIONAL_TO_SIMPLE_MAP.put("九", "九");
        TRADITIONAL_TO_SIMPLE_MAP.put("二", "二");
        TRADITIONAL_TO_SIMPLE_MAP.put("五", "五");
        TRADITIONAL_TO_SIMPLE_MAP.put("億", "亿");
        TRADITIONAL_TO_SIMPLE_MAP.put("兆", "兆");
        TRADITIONAL_TO_SIMPLE_MAP.put("兩", "两");
        TRADITIONAL_TO_SIMPLE_MAP.put("八", "八");
        TRADITIONAL_TO_SIMPLE_MAP.put("六", "六");
        TRADITIONAL_TO_SIMPLE_MAP.put("十", "十");
        TRADITIONAL_TO_SIMPLE_MAP.put("千", "千");
        TRADITIONAL_TO_SIMPLE_MAP.put("四", "四");
        TRADITIONAL_TO_SIMPLE_MAP.put("百", "百");
        TRADITIONAL_TO_SIMPLE_MAP.put("萬", "万");
        TRADITIONAL_TO_SIMPLE_MAP.put("負", "负");
        TRADITIONAL_TO_SIMPLE_MAP.put("零", "零");
        TRADITIONAL_TO_SIMPLE_MAP.put("點", "点");
    }

    /**
     * 将英文表示的数字转化为中文表示的数字，支持负数、小数、不支持分数
     *
     * @param text 英文表示的数字，比如：2009000，5.3，-5.3
     * @return
     */
    public static String englishNumberToChinese(String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("empty input");
        }
        boolean negative = false;
        if (text.length() == 1 && text.charAt(0) == '0') {
            return DIGITS[0];
        }
        if (text.charAt(0) == '-') {
            negative = true;
            text = text.substring(1);
        }
        Matcher m = ENGLISH_DECIMAL_PATTERN.matcher(text);
        String result;
        if (m.find()) {
            result = englishNumberToChineseFull(m.group(1)) + DECIMAL + englishNumberToChineseBrief(m.group(2));
        } else {
            m = ENGLISH_FRACTION_PATTERN.matcher(text);
            if (m.find()) {
                result = englishNumberToChineseFull(m.group(2)) + FRACTION + englishNumberToChineseFull(m.group(1));
            } else {
                result = englishNumberToChineseFull(text);
            }
        }

        if (negative) {
            result = MINUS + result;
        }
        return result;
    }

    /**
     * 直接映射英文数字为中文数字，对应输入的小数部分如此处理
     * @param text
     * @return
     */
    private static String englishNumberToChineseBrief(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            result += DIGITS[text.charAt(i) - '0'];
        }
        return result;
    }

    /**
     * 非输入的小数部分需要做更复杂的转换
     * @param text
     * @return
     */
    private static String englishNumberToChineseFull(String text) {
        int power = 0;
        boolean canAddZero = false;
        boolean inZero = false;
        Map<Integer, Integer> powers = new HashMap<>();
        long number = Long.parseLong(text);
        while (Math.pow(10, power) <= number) {
            int value = (int) ((number % (Math.pow(10, power + 1))) / (Math.pow(10, power)));
            powers.put(power, value);
            number -= number % (Math.pow(10, power + 1));
            power++;
        }
        String result = "";
        for (int i = 0; i < power; i++) {
            if (i % 4 == 0) {
                if (powers.get(i) != 0) {
                    inZero = false;
                    canAddZero = true;
                    result = DIGITS[powers.get(i)] + AFTER_WAN_DIGITS[i / 4] + result;
                } else {
                    if (((i + 3 < power) && powers.get(i + 3) != 0) ||
                            ((i + 2 < power) && powers.get(i + 2) != 0) ||
                            ((i + 1 < power) && powers.get(i + 1) != 0)) {
                        result = AFTER_WAN_DIGITS[i / 4] + result;
                        canAddZero = false;
                    }
                }
            } else {
                if (powers.get(i) != 0) {
                    inZero = false;
                    canAddZero = true;
                    if (power == 2 && i == 1 && powers.get(i) == 1) {
                        result = BEFORE_WAN_DIGITS[(i % 4) - 1] + result;
                    } else {
                        result = DIGITS[powers.get(i)] + BEFORE_WAN_DIGITS[(i % 4) - 1] + result;
                    }
                } else {
                    if (canAddZero && !inZero) {
                        inZero = true;
                        result = DIGITS[powers.get(i)] + result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param text 输入中文数字，支持正负数、小数、分数，比如：五千四百九十一万四千七百一十
     * @return 转化为double的结果
     */
    public static double chineseNumberToEnglish(String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("empty input");
        }
        double result;
//        boolean ordinal = false;
//        if (text.startsWith("第")) {
//            ordinal = true;
//        }
        if (text.contains(FRACTION)) {
            int idx = text.indexOf(FRACTION);
            result = chineseToEnglishFull(text.substring(idx + 2)) / chineseToEnglishFull(text.substring(0, idx));
        } else if (text.length() > 1) {
            if (DIGITS_PATTERN.matcher(text).find()) {
                result = chineseToEnglishBrief(text);
            } else {
                result = chineseToEnglishFull(text);
            }
        } else {
            result = chineseToEnglishFull(text);
        }
        return result;
    }

    /**
     * 输入如果完全匹配中文对应的数字，直接调用此函数映射输出
     * @param text
     * @return
     */
    private static long chineseToEnglishBrief(String text) {
        char[] chars = text.toCharArray();
        long total = 0;
        for (int i = 0; i < chars.length; i++) {
            total *= 10;
            total += DIGITS_MAP.get(chars[i]);
        }
        return total;
    }

    /**
     * 对应复杂的中文数字串
     * @param text
     * @return
     */
    private static double chineseToEnglishFull(String text) {
        text = text.replace("万亿", "兆");
        text = text.replace("萬億", "兆");
        text = text.replace("亿万", "兆");
        text = text.replace("億萬", "兆");
        text = text.replace("個", "");
        text = text.replace("个", "");
        text = text.replace("廿", "二十");
        text = text.replace("卄", "二十");
        text = text.replace("卅", "三十");
        text = text.replace("卌", "四十");
        double total = 0;
        /**
         * 兆，万，万以下分别是不同的level，每个level的sum存储在levelTotal中
         */
        double levelTotal = 0;
        /**
         * 当前字符的数值
         */
        long digitVal;
        /**
         * 是否为负数
         */
        boolean negative = false;
        int power = 0;
        /**
         * 是否小数
         */
        boolean afterDecimal = false;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i == 0 && (c == '负' || c == '負' || c == '-')) { // 负数，跳过首字母，记录标记位，剩下部分转化为英文数字
                negative = true;
                continue;
            } else if (i == 0 && c == '第') { // 序数，跳过首字母，后面部分转化成英文数字
                continue;
            } else if (c == '點' || c == '点' || c == '.' || c == '．') {
                afterDecimal = true;
                power = -1; // 小数点之后的第一位是10^-1，第二位是10^-2，以此类推
            } else if (c == '兆') { // 遇到一个level层级兆，levelTotal清0
                power = 12;
                if (levelTotal == 0) {
                    levelTotal = 1;
                }
                total += levelTotal * Math.pow(10, power);
                levelTotal = 0;
                power -= 4;
            } else if (c == '万' || c == '萬') { // 遇到一个level层级万，levelTotal清0
                power = 4;
                if (levelTotal == 0) {
                    levelTotal = 1;
                }
                total += levelTotal * Math.pow(10, power);
                levelTotal = 0;
                power -= 4;
            } else if (c == '千' || c == '仟') {// 千五,百五这种
                levelTotal += 1000;
            } else if (c == '百' || c == '佰') {
                levelTotal += 100;
            } else if (c == '十' || c == '拾') { // 十四万零九百
                levelTotal += 10;
            } else if (c == '零' || c == '〇' || c == '0' || c == '０') {
                power = 0;
            } else if (DIGITS_MAP.containsKey(c)) {
                digitVal = DIGITS_MAP.get(c);
                if (afterDecimal) { // 小数点后面应该都是可以直接转化为数字的中文数字
                    levelTotal += digitVal * Math.pow(10, power);
                    power--;
                    while (i + 1 < chars.length && DIGITS_MAP.containsKey(chars[i + 1])) {
                        levelTotal += DIGITS_MAP.get(chars[i + 1]) * Math.pow(10, power);
                        power--;
                        i++;
                    }
                } else if (i + 1 < chars.length) { // 对于 x十， x百， x千这些，x只可能是一位数字，所以可以直接转换
                    char nextChar = chars[i + 1];
                    if (nextChar == '十' || nextChar == '拾') {
                        levelTotal += digitVal * 10;
                        i++;
                    } else if (nextChar == '百' || nextChar == '佰') {
                        levelTotal += digitVal * 100;
                        i++;
                    } else if (nextChar == '千' || nextChar == '仟') {
                        levelTotal += digitVal * 1000;
                        i++;
                    } else if (DIGITS_MAP.containsKey(nextChar)) {
                        levelTotal *= 10;
                        levelTotal += digitVal;
                        while (i + 1 < chars.length && DIGITS_MAP.containsKey(chars[i + 1])) {
                            levelTotal *= 10;
                            levelTotal += DIGITS_MAP.get(chars[i + 1]);
                            i++;
                        }
                    } else {
                        levelTotal += digitVal;
                    }
                } else { // i+1>=text.lenghth
                    if (i > 0) {//处理这样的数字串的最后一位：三百五，五千三
                        char prevChar = chars[i - 1];
                        if (prevChar == '兆') {
                            levelTotal += digitVal * Math.pow(10, 11);
                        } else if (prevChar == '亿' || prevChar == '億') {
                            levelTotal += digitVal * Math.pow(10, 7);
                        } else if (prevChar == '萬' || prevChar == '万') {
                            levelTotal += digitVal * 1000;
                        } else if (prevChar == '千' || prevChar == '仟') {
                            levelTotal += digitVal * 100;
                        } else if (prevChar == '百' || prevChar == '佰') {
                            levelTotal += digitVal * 10;
                        } else {
                            levelTotal += digitVal;
                        }
                    } else {
                        levelTotal += digitVal;
                    }
                }
            } else {
                LOGGER.debug("bad input number string:{}", text);
            }
        }
        total += levelTotal;
        if (negative) {
            total = -total;
        }
        return total;
    }

    public static void main(String[] args) {
        double total = ChineseNumbers.chineseToEnglishFull("四千五百零四万四千三百一十八");
        LOGGER.debug("{}", total);
    }
}
