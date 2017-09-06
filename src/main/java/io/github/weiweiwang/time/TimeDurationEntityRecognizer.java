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
public class TimeDurationEntityRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDurationEntityRecognizer.class);
    private static final long YEAR_IN_SECONDS = 365 * 24 * 60 * 60;
    private static final long MONTH_IN_SECONDS = 30 * 24 * 60 * 60;
    private static final long WEEK_IN_SECONDS = 30 * 24 * 60 * 60;
    private static final long DAY_IN_SECONDS = 24 * 60 * 60;
    private static final long HOUR_IN_SECONDS = 60 * 60;
    private static final long MINUTE_IN_SECONDS = 60;


    private Pattern pattern;
    private List<String> regexList;

    public TimeDurationEntityRecognizer() throws IOException {
        this(TimeDurationEntityRecognizer.class.getResourceAsStream("/duration.regex"));
    }

    public TimeDurationEntityRecognizer(String file) throws IOException {
        this(new FileInputStream(file));
    }

    public TimeDurationEntityRecognizer(InputStream in) throws IOException {
        regexList = IOUtils.readLines(in, "UTF-8").stream().map(StringUtils::stripToNull)
                .filter(item -> StringUtils.isNotEmpty(item) && !item.startsWith("#")).distinct()
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

    public List<TimeDurationEntity> parse(String text) {
        List<TimeDurationEntity> result = new ArrayList<>();
        int offset;
        Matcher match = pattern.matcher(text);
        while (match.find()) {
            TimeDurationEntity lastEntity = result.isEmpty() ? null : result.get(result.size() - 1);
            offset = match.start();
            String matchedText = match.group();
            if (lastEntity != null && offset == lastEntity.getOffset() + lastEntity.getOriginal().length()) {
                lastEntity.setOriginal(lastEntity.getOriginal() + matchedText);
            } else {
                TimeDurationEntity timeDurationEntity = new TimeDurationEntity(matchedText, offset);
                result.add(timeDurationEntity);
            }
        }
        Iterator<TimeDurationEntity> iterator = result.iterator();
        while (iterator.hasNext()) {
            TimeDurationEntity timeDurationEntity = iterator.next();
            Long duration = parseDuration(timeDurationEntity.getOriginal());
            if (null != duration) {
                timeDurationEntity.setValue(duration);
            } else {
                iterator.remove();
            }
        }
        return result;
    }

    private String normalizeDurationString(String text) {
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

    private Long parseDuration(String text) {
        text = normalizeDurationString(text);
        long year = parseYear(text);
        long month = parseMonth(text);
        long day = parseDay(text);
        long hour = parseHour(text);
        long minute = parseMinute(text);
        long[] arr = {year, month, day, hour, minute,};
        if (!validTime(arr)) {
            return null;
        }
        return Arrays.stream(arr).filter(item -> item > 0).sum();
    }

    private boolean validTime(long arr[]) {
        long sum = Arrays.stream(arr).sum();
        if (sum <= -5) {
            return false;
        }
        return true;
    }

    /**
     * 将第一个下标对应数值为正的前面几个字段都设置为相对时间的对应值
     *
     * @param arr
     * @param relative
     */
    private void normalize(int[] arr, Date relative, boolean isDefaultRelative) {
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 0) {
                j = i;
                break;
            }
        }
        Calendar calender = Calendar.getInstance();

        //如果没有相对日期约束，时间又是过去的时间，设置为最近的一个未来时间
        if (isDefaultRelative && arr[2] < 0 && arr[3] < calender.get(Calendar.HOUR_OF_DAY)) {
            arr[3] += 12;
        }

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


    private static final Pattern YEAR_DURATION_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十])(年)");

    private long parseYear(String text) {
        long year = -1;
        if (text.contains("半年")) {
            year = YEAR_IN_SECONDS / 2;
        }

        Matcher match = YEAR_DURATION_PATTERN.matcher(text);
        if (match.find()) {
            year = Integer.parseInt(match.group(1)) * YEAR_IN_SECONDS;
        }

        return year;
    }

    private static final Pattern HALF_MONTH_DURATION_PATTERN = Pattern.compile("(半个?(月))");
    private static final Pattern MONTH_DURATION_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十]+)个?(月)");

    private long parseMonth(String text) {
        long month = -1;
        Matcher match = HALF_MONTH_DURATION_PATTERN.matcher(text);
        if (match.find()) {
            month = MONTH_IN_SECONDS / 2;
        } else {
            match = MONTH_DURATION_PATTERN.matcher(text);
            if (match.find()) {
                month = Integer.parseInt(match.group(1)) * MONTH_IN_SECONDS;
            }
        }
        return month;
    }

    private static final Pattern WEEK_DURATION_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十]+)(周)");

    private long parseWeek(String text) {
        long week = -1;
        Matcher match = WEEK_DURATION_PATTERN.matcher(text);
        week = Integer.parseInt(match.group(1)) * WEEK_IN_SECONDS;
        return week;
    }

    private static final Pattern DAY_DURATION_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十]+)天");

    private long parseDay(String text) {
        long day = -1;
        if (text.contains("半天")) {
            day = DAY_IN_SECONDS / 2;
        } else {
            Matcher match = DAY_DURATION_PATTERN.matcher(text);
            if (match.find()) {
                day = Integer.parseInt(match.group(1)) * DAY_IN_SECONDS;
            }
        }
        return day;
    }


    private static final Pattern HALF_HOUR_DURATION_PATTERN = Pattern.compile("(半个?(小时|钟头))");
    private static final Pattern HOUR_DURATION_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十]+)个?(小时|钟头)");

    private long parseHour(String text) {
        Matcher match = HALF_HOUR_DURATION_PATTERN.matcher(text);
        long hour = -1;
        if (match.find()) {
            hour = HOUR_IN_SECONDS / 2;
        } else {
            match = HOUR_DURATION_PATTERN.matcher(text);
            if (match.find()) {
                hour = Integer.parseInt(match.group(1)) * HOUR_IN_SECONDS;
            }
        }
        return hour;
    }


    private static final Pattern MINUTE_PATTERN = Pattern.compile("([0-9一二两三四五六七八九十]+)(分钟)");

    /**
     * @param text
     * @return
     */
    private long parseMinute(String text) {
        /*
         * 添加了省略“分”说法的时间
		 * 如17点15
		 * modified by 曹零
		 */
        long minute = -1;
        Matcher match = MINUTE_PATTERN.matcher(text);
        if (match.find()) {
            minute = Integer.parseInt(match.group(1)) * MINUTE_IN_SECONDS;
        }
        return minute;
    }
}
