package io.github.weiweiwang.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cycle {
    public static final int CYCLE_TYPE_DAY = 0;
    public static final int CYCLE_TYPE_WEEK = 1;
    public static final int CYCLE_TYPE_MONTH = 2;
    public static final int CYCLE_TYPE_YEAR = 3;

    private int type;
    private int nth;

    public Cycle(int type, int nth) {
        this.type = type;
        this.nth = nth;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNth() {
        return nth;
    }

    public void setNth(int nth) {
        this.nth = nth;
    }

    public String toString() {
        return String.format("[type=%d, nth=%d]", type, nth);
    }

    private static Pattern WEEKLY_PATTERN = Pattern.compile("每周(\\d)");
    private static Pattern MONTHLY_PATTERN = Pattern.compile("每月([0-3]?[0-9])[日号]");

    public static Cycle parseCycle(String text) {
        if (text.contains("每天")) {
            return new Cycle(CYCLE_TYPE_DAY, -1);
        }

        Matcher matcher = WEEKLY_PATTERN.matcher(text);
        if (matcher.find()) {
            int nth = Integer.parseInt(matcher.group(1));
            if (nth >= 1 && nth <= 7) {
                return new Cycle(CYCLE_TYPE_WEEK, nth);
            }
        }

        matcher = MONTHLY_PATTERN.matcher(text);
        if (matcher.find()) {
            int nth = Integer.parseInt(matcher.group(1));
            if (nth >= 1 && nth <= 31) {
                return new Cycle(CYCLE_TYPE_MONTH, nth);
            }
        }

        return null;
    }
}