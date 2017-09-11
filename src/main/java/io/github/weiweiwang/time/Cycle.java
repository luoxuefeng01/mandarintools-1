package io.github.weiweiwang.time;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cycle {
    public static final int CYCLE_TYPE_NONE = 0;
    public static final int CYCLE_TYPE_DAY = 1;
    public static final int CYCLE_TYPE_WEEK = 2;
    public static final int CYCLE_TYPE_MONTH = 3;
    public static final int CYCLE_TYPE_YEAR = 4;
    private static final Map<Integer, String> TYPE_DESC_DICT = new HashMap<Integer, String>() {{
        put(CYCLE_TYPE_NONE, "None");
        put(CYCLE_TYPE_DAY, "Day");
        put(CYCLE_TYPE_WEEK, "Week");
        put(CYCLE_TYPE_MONTH, "Month");
        put(CYCLE_TYPE_YEAR, "Year");
    }};

    private int type = CYCLE_TYPE_NONE;
//    private int nth;

    public Cycle(int type) {
        this.type = type;
//        this.nth = nth;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public int getNth() {
//        return nth;
//    }

//    public void setNth(int nth) {
//        this.nth = nth;
//    }

    public String toString() {
        return String.format("[type=%s]", TYPE_DESC_DICT.containsKey(type) ? TYPE_DESC_DICT.get(type) : "Unknown");
    }

    private static Pattern WEEKLY_PATTERN = Pattern.compile("每周(\\d)");
    private static Pattern MONTHLY_PATTERN = Pattern.compile("每月([0-3]?[0-9])[日号]");
    private static Pattern YEAR_PATTERN = Pattern.compile("每年([0-1]?[0-9])月([0-3]?[0-9])[日号]");

    public static Cycle parseCycle(String text) {
        if (text.contains("每天")) {
            return new Cycle(CYCLE_TYPE_DAY);
        }

        Matcher matcher = WEEKLY_PATTERN.matcher(text);
        if (matcher.find()) {
            int nth = Integer.parseInt(matcher.group(1));
            if (nth >= 1 && nth <= 7) {
                return new Cycle(CYCLE_TYPE_WEEK);
            }
        }

        matcher = MONTHLY_PATTERN.matcher(text);
        if (matcher.find()) {
            int nth = Integer.parseInt(matcher.group(1));
            if (nth >= 1 && nth <= 31) {
                return new Cycle(CYCLE_TYPE_MONTH);
            }
        }
        matcher = YEAR_PATTERN.matcher(text);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group(1));
            int day = Integer.parseInt(matcher.group(2));
            if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                return new Cycle(CYCLE_TYPE_YEAR);
            }
        }

        return null;
    }
}