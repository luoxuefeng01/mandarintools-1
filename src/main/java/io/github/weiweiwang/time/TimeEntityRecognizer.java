package io.github.weiweiwang.time;

import io.github.weiweiwang.number.ChineseNumbers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by wangweiwei01 on 17/4/19.
 */

/**
 * thread safe
 */
public class TimeEntityRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeEntityRecognizer.class);
    private Pattern pattern;
    private List<String> regexList;

    public TimeEntityRecognizer() throws IOException {
        this(TimeEntityRecognizer.class.getResourceAsStream("/time.regex"));
    }

    public TimeEntityRecognizer(String file) throws IOException {
        this(new FileInputStream(file));
    }

    public TimeEntityRecognizer(InputStream in) throws IOException {
        regexList = IOUtils.readLines(in, "UTF-8").stream().map(StringUtils::stripToNull)
                .filter(item -> StringUtils.isNotEmpty(item) && !item.startsWith("#")).distinct()
//                .sorted((o1, o2) -> o2.length() - o1.length())
                .collect(Collectors.toList());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("input regex[size={}, text={}]", regexList.size(), regexList);
        }
        long start = System.currentTimeMillis();
        this.pattern = Pattern.compile(regexList.stream().map(item -> "(" + item + ")").collect(Collectors.joining("|")));
        long end = System.currentTimeMillis();
        LOGGER.info("pattern initialized for {} patterns, time used(ms):{}", regexList.size(),
                (end - start));
    }

    public List<TimeEntity> parse(String text) {
        return parse(text, Calendar.getInstance().getTime());
    }

    public List<TimeEntity> parse(String text, Date relative) {
        List<TimeEntity> result = new ArrayList<>();
        int offset;
        Matcher match = pattern.matcher(text);
        while (match.find()) {
            TimeEntity lastEntity = result.isEmpty() ? null : result.get(result.size() - 1);
            offset = match.start();
            String matchedText = match.group();
            if (lastEntity != null && offset == lastEntity.getOffset() + lastEntity.getOriginal().length()) {
                lastEntity.setOriginal(lastEntity.getOriginal() + matchedText);
            } else {
                TimeEntity timeEntity = new TimeEntity(matchedText, offset);
                result.add(timeEntity);
            }
        }
        Iterator<TimeEntity> iterator = result.iterator();
        while (iterator.hasNext()) {
            TimeEntity timeEntity = iterator.next();
            Date date = parseTime(timeEntity.getOriginal(), relative);
            if (null != date) {
                timeEntity.setValue(date);
            } else {
                iterator.remove();
            }
        }
        return result;
    }


    private static final Pattern YEAR_2_DIGIT_PATTERN = Pattern.compile("[0-9]{2}(?=年)");
    private static final Pattern YEAR_4_DIGIT_PATTERN = Pattern.compile("[0-9]?[0-9]{3}(?=年)");

    private String normalizeTimeString(String text) {
        text = text.replace("周日", "周7").replace("：", ":");
        Pattern p = Pattern.compile("[一二两三四五六七八九十]+");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            String group = m.group();
            Number number = ChineseNumbers.chineseNumberToEnglish(group);
            m.appendReplacement(sb, String.valueOf(number.intValue()));
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private boolean validTime(int arr[]) {
        int sum = Arrays.stream(arr).sum();
        //month
        if (arr[1] > 12) {
            return false;
        }
        //day
        if (arr[2] > 31) {
            return false;
        }
        //hour
        if (arr[3] > 23) {
            return false;
        }
        //minute
        if (arr[4] > 59) {
            return false;
        }
        //second
        if (arr[5] > 59) {
            return false;
        }
        return sum != -6;
    }

    private Date parseTime(String text, Date relative) {
        text = normalizeTimeString(text);
        int year = parseYear(text);
        int month = parseMonth(text);
        int day = parseDay(text);
        int hour = parseHour(text);
        int minute = parseMinute(text);
        int second = parseSecond(text);
        int[] arr = {year, month, day, hour, minute, second};
        overallParse(text, arr);
        parseRelative(text, relative, arr);
        parseCurrentRelative(text, relative, arr);

        if (!validTime(arr)) {
            return null;
        }
        normalize(arr, relative);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        final int[] fields = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar
                .MINUTE, Calendar.SECOND};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                calendar.set(fields[i], arr[i]);
            }
        }
        if (arr[1] > 0) {
            calendar.set(Calendar.MONTH, arr[1] - 1);
        }
        return calendar.getTime();
    }

    /**
     * 将第一个下标对应数值为正的前面几个字段都设置为相对时间的对应值
     *
     * @param arr
     * @param relative
     */
    private void normalize(int[] arr, Date relative) {
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                j = i;
                break;
            }
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(relative);
        final int[] fields = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar
                .MINUTE, Calendar.SECOND};
        for (int i = 0; i < j; i++) {
            if (arr[i] < 0) {
                if (i == 1) {
                    arr[i] = calender.get(Calendar.MONTH) + 1;
                } else {
                    arr[i] = calender.get(fields[i]);
                }
            }
        }
    }

    private int parseYear(String text) {
        int year = -1;
        /*
         * 不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份
		 * modified by 曹零
		 */
        Matcher match = YEAR_4_DIGIT_PATTERN.matcher(text);
        if (match.find()) {
            year = Integer.parseInt(match.group());
        } else {
            match = YEAR_2_DIGIT_PATTERN.matcher(text);
            if (match.find()) {
                year = Integer.parseInt(match.group());
                if (year >= 0 && year < 100) {
                    if (year < 30) {
                        year += 2000;
                    } else {
                        year += 1900;
                    }
                }

            }
        }

        return year;
    }

    private static final Pattern MONTH_PATTERN = Pattern.compile("((?<!\\d))((10)|(11)|(12)|([1-9]))(?=月)");

    private int parseMonth(String text) {
        Matcher match = MONTH_PATTERN.matcher(text);
        if (match.find()) {
            return Integer.parseInt(match.group());
        }
        return -1;
    }

    private static final Pattern DAY_PATTERN = Pattern.compile("(((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号)))|((?<=月)([0-3][0-9]|[1-9])(?=(日|号))?)");
//    private static final Pattern MONTH_DAY_PATTERN = Pattern.compile("(?<=月)([0-3][0-9]|[1-9])(?=(日|号))?");

    private int parseDay(String text) {
        Matcher match = DAY_PATTERN.matcher(text);
        if (match.find()) {
//            int g = match.groupCount();
//            for (int i = 0; i < g; i++) {
//                String group = match.group(i);
//                LOGGER.debug("group {}, str:{}", i, group);
//            }
            return Integer.parseInt(match.group());
        }
        return -1;
    }


    private static final Pattern HOUR_PATTERN = Pattern.compile("(?<!(周|星期|\\d))([0-2]?[0-9])(?=(点|时))");
    private static final Pattern NOON_PATTERN = Pattern.compile("(中午)|(午间)");
    private static final Pattern AFTERNOON_PATTERN = Pattern.compile("(下午)|(午后)|(pm)|(PM)");
    private static final Pattern NIGHT_PATTERN = Pattern.compile("晚");

    /**
     * @param text
     * @return
     */
    private int parseHour(String text) {
        /*
         * 清除只能识别11-99时的bug
		 * modified by 曹零
		 */

        int hour = -1;
        Matcher match = HOUR_PATTERN.matcher(text);
        if (match.find()) {
            hour = Integer.parseInt(match.group());
        }
        /*
         * 对关键字：中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确24小时时间计算
		 * 规约：
		 * 1.中午/午间0-10点视为12-22点
		 * 2.下午/午后0-11点视为12-23点
		 * 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点
		 * 4.0-11点pm/PM视为12-23点
		 *
		 * add by 曹零
		 */
        match = NOON_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 0 && hour <= 10)
                hour += 12;
        }

        match = AFTERNOON_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 0 && hour <= 11)
                hour += 12;
        }

        match = NIGHT_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 1 && hour <= 11)
                hour += 12;
            else if (hour == 12)
                hour = 0;
        }
        return hour;
    }

    private static final Pattern MINUTE_PATTERN = Pattern.compile("([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!(周|星期|\\d))([0-2]?[0-9])(点|时)))[0-5]?[0-9](?!刻))");
    private static final Pattern ONE_QUARTER_PATTERN = Pattern.compile("(?<=[点时])[1一]刻(?!钟)");
    private static final Pattern TWO_QUARTER_PATTERN = Pattern.compile("(?<=[点时])半");
    private static final Pattern THREE_QUARTER_PATTERN = Pattern.compile("(?<=[点时])[3三]刻(?!钟)");

    /**
     * @param text
     * @return
     */
    private int parseMinute(String text) {
        /*
         * 添加了省略“分”说法的时间
		 * 如17点15
		 * modified by 曹零
		 */
        int minute = -1;
        Matcher match = MINUTE_PATTERN.matcher(text);
        if (match.find()) {
            minute = Integer.parseInt(match.group());
        }
        /*
         * 添加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）
		 *
		 * add by 曹零
		 */
        match = ONE_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 15;
        }

        match = TWO_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 30;
        }

        match = THREE_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 45;
        }
        return minute;
    }

    private static final Pattern SECOND_PATTERN = Pattern.compile("([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])");

    private int parseSecond(String text) {
        /*
         * 添加了省略“分”说法的时间
		 * 如17点15分32
		 * modified by 曹零
		 */
        Matcher match = SECOND_PATTERN.matcher(text);
        if (match.find()) {
            return Integer.parseInt(match.group());
        }
        return -1;
    }

    private static final Pattern HOUR_MINUTE_SECOND_PATTERN = Pattern
            .compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]");

    private static final Pattern HOUR_MINUTE_PATTERN = Pattern.compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]");

    private static final Pattern DASH_YEAR_MONTH_DAY = Pattern
            .compile("[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])");

    private static final Pattern SLASH_YEAR_MONTH_DAY = Pattern
            .compile("((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}");
    private static final Pattern DOT_YEAR_MONTH_DAY = Pattern
            .compile("[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])");

    private void overallParse(String text, int[] arr) {
        /*
         * 修改了函数中所有的匹配规则使之更为严格
		 * modified by 曹零
		 */
        Matcher match = HOUR_MINUTE_SECOND_PATTERN.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split(":");
            arr[3] = Integer.parseInt(splits[0]);
            arr[4] = Integer.parseInt(splits[1]);
            arr[5] = Integer.parseInt(splits[2]);
        } else {
        /*
         * 添加了省略秒的:固定形式的时间规则匹配
		 * add by 曹零
		 */
            match = HOUR_MINUTE_PATTERN.matcher(text);
            if (match.find()) {
                String[] splits = match.group().split(":");
                arr[3] = Integer.parseInt(splits[0]);
                arr[4] = Integer.parseInt(splits[1]);
            }
        }
        /*
         * 增加了:固定形式时间表达式的
		 * 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM
		 * 的正确时间计算，规约同上
		 * add by 曹零
		 */
        match = NOON_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 0 && arr[3] <= 10)
                arr[3] += 12;
        }

        match = AFTERNOON_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 0 && arr[3] <= 11)
                arr[3] += 12;
        }

        match = NIGHT_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 1 && arr[3] <= 11)
                arr[3] += 12;
            else if (arr[3] == 12)
                arr[3] = 0;
        }


        match = DASH_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("-");
            arr[0] = Integer.parseInt(splits[0]);
            arr[1] = Integer.parseInt(splits[1]);
            arr[2] = Integer.parseInt(splits[2]);
        }

        match = SLASH_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("/");
            arr[1] = Integer.parseInt(splits[0]);
            arr[2] = Integer.parseInt(splits[1]);
            arr[0] = Integer.parseInt(splits[2]);
        }

		/*
         * 增加了:固定形式时间表达式 年.月.日 的正确识别
		 * add by 曹零
		 */
        match = DOT_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("\\.");
            arr[0] = Integer.parseInt(splits[0]);
            arr[1] = Integer.parseInt(splits[1]);
            arr[2] = Integer.parseInt(splits[2]);
        }
    }

    private static final Pattern DAYS_BEFORE_PATTERN = Pattern.compile("\\d+(?=天[以之]?前)");
    private static final Pattern DAYS_AFTER_PATTERN = Pattern.compile("\\d+(?=天[以之]?后)");
    private static final Pattern MONTH_BEFORE_PATTERN = Pattern.compile("\\d+(?=(个)?月[以之]?前)");
    private static final Pattern MONTH_AFTER_PATTERN = Pattern.compile("\\d+(?=(个)?月[以之]?后)");
    private static final Pattern YEAR_BEFORE_PATTERN = Pattern.compile("\\d+(?=年[以之]?前)");
    private static final Pattern YEAR_AFTER_PATTERN = Pattern.compile("\\d+(?=年[以之]?后)");

    private void parseRelative(String text, Date relative, int[] arr) {

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(relative);
        boolean[] flag = {false, false, false};

        Matcher match = DAYS_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int day = Integer.parseInt(match.group());
            calendar.add(Calendar.DATE, -day);
            flag[2] = true;
        }

        match = DAYS_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int day = Integer.parseInt(match.group());
            calendar.add(Calendar.DATE, day);
            flag[2] = true;
        }

        match = MONTH_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int month = Integer.parseInt(match.group());
            calendar.add(Calendar.MONTH, -month);
            flag[1] = true;
        }

        match = MONTH_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int month = Integer.parseInt(match.group());
            calendar.add(Calendar.MONTH, month);
            flag[1] = true;
        }

        match = YEAR_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int year = Integer.parseInt(match.group());
            calendar.add(Calendar.YEAR, -year);
            flag[0] = true;
        }

        match = YEAR_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int year = Integer.parseInt(match.group());
            calendar.add(Calendar.YEAR, year);
            flag[0] = true;
        }
//        if (!relative.equals(calendar.getTime())) {
//            arr[0] = calendar.get(Calendar.YEAR);
//            arr[1] = calendar.get(Calendar.MONTH) + 1;
//            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
//        }
        if (flag[0] || flag[1] || flag[2]) {
            arr[0] = calendar.get(Calendar.YEAR);
        }
        if (flag[1] || flag[2]) {
            arr[1] = calendar.get(Calendar.MONTH) + 1;
        }
        if (flag[2]) {
            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }


    private static final Pattern LAST_MONTH_PATTERN = Pattern.compile("上(个)?月");
    private static final Pattern THIS_MONTH_PATTERN = Pattern.compile("(本|这个)月");
    private static final Pattern NEXT_MONTH_PATTERN = Pattern.compile("下(个)?月");
    private static final Pattern DAY_BEFORE_YESTERDAY_PATTERN = Pattern.compile("(?<!大)前天");
    private static final Pattern TODAY_PATTERN = Pattern.compile("今(?!年)");
    private static final Pattern TOMORROW_PATTERN = Pattern.compile("明(?!年)");
    private static final Pattern DAY_AFTER_TOMORROW_PATTERN = Pattern.compile("(?<!大)后天");
    private static final Pattern BEFORE_LAST_WEEKDAY_PATTERN = Pattern.compile("(?<=(上上(周|星期)))[1-7]");
    private static final Pattern LAST_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!上)上(周|星期)))[1-7]");
    private static final Pattern NEXT_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!下)下(周|星期)))[1-7]");
    private static final Pattern NEXT_NEXT_WEEKDAY_PATTERN = Pattern.compile("(?<=(下下(周|星期)))[1-7]");
    private static final Pattern THIS_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!(上|下))(周|星期)))[1-7]");

    /**
     * 设置当前时间相关的时间表达式
     * <p>
     * add by 曹零
     */
    public void parseCurrentRelative(String text, Date relative, int[] arr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(relative);

        boolean[] flag = {false, false, false};//观察时间表达式是否因当前相关时间表达式而改变时间

        if (text.contains("前年")) {
            calendar.add(Calendar.YEAR, -2);
            flag[0] = true;
        }

        if (text.contains("去年")) {
            calendar.add(Calendar.YEAR, -1);
            flag[0] = true;
        }

        if (text.contains("今年")) {
            calendar.add(Calendar.YEAR, 0);
            flag[0] = true;
        }

        if (text.contains("明年")) {
            calendar.add(Calendar.YEAR, 1);
            flag[0] = true;
        }

        if (text.contains("后年")) {
            calendar.add(Calendar.YEAR, 2);
            flag[0] = true;
        }

        Matcher match = LAST_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, -1);
            flag[1] = true;
        }

        match = THIS_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, 0);
            flag[1] = true;
        }

        match = NEXT_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, 1);
            flag[1] = true;
        }

        if (text.contains("大大前天")) {
            calendar.add(Calendar.DATE, -4);
            flag[2] = true;
        } else if (text.contains("大前天")) {
            calendar.add(Calendar.DATE, -3);
            flag[2] = true;
        }

        match = DAY_BEFORE_YESTERDAY_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, -2);
            flag[2] = true;
        }

        if (text.contains("昨")) {
            calendar.add(Calendar.DATE, -1);
            flag[2] = true;
        }

        match = TODAY_PATTERN.matcher(text);
        if (match.find()) {
            flag[2] = true;
        }

        match = TOMORROW_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, 1);
            flag[2] = true;
        }

        match = DAY_AFTER_TOMORROW_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, 2);
            flag[2] = true;
        }

        if (text.contains("大大后天")) {
            calendar.add(Calendar.DATE, 4);
            flag[2] = true;
        } else if (text.contains("大后天")) {
            calendar.add(Calendar.DATE, 3);
            flag[2] = true;
        }

        match = BEFORE_LAST_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) { // 周日=1，周二=2，。。。， 周六=7
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, -2);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }

        match = LAST_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }

//        rule = "(?<=((?<!下)下(周|星期)))[1-7]";
        match = NEXT_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;

        }

//        rule = "(?<=(下下(周|星期)))[1-7]";
        match = NEXT_NEXT_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 2);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;

        }

//        rule = "(?<=((?<!(上|下))(周|星期)))[1-7]";
        match = THIS_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
//            calendar.add(Calendar.WEEK_OF_MONTH, 0);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }
//        if (!relative.equals(calendar.getTime()) || text.contains("今")) {
//            arr[0] = calendar.get(Calendar.YEAR);
//            arr[1] = calendar.get(Calendar.MONTH) + 1;
//            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
//        }
//        String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
        if (flag[0] || flag[1] || flag[2]) {
            arr[0] = calendar.get(Calendar.YEAR);
        }
        if (flag[1] || flag[2]) {
            arr[1] = calendar.get(Calendar.MONTH) + 1;
        }
        if (flag[2]) {
            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }
}
