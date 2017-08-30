package io.github.weiweiwang.time;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by wangweiwei01 on 17/4/19.
 */
public class TimeDurationEntity {
    private String original;
    private long value;
    private int offset;

    public TimeDurationEntity(String original, int offset) {
        this.original = original;
        this.offset = offset;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("offset", offset).
                append("original", original).
                append("value", value / 60 + "m").
                toString();
    }
}
