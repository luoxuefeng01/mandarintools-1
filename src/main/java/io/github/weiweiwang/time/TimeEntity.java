package io.github.weiweiwang.time;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangweiwei01 on 17/4/19.
 */
public class TimeEntity {
    private String original;
    private Date value;
    private int offset;

    public TimeEntity(String original, int offset) {
        this.original = original;
        this.offset = offset;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
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
                append("value", value != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) : value).
                toString();
    }
}
