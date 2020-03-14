package com.jxust.qq.superquestionlib.util;


import java.time.format.DateTimeFormatter;

public enum DateFormat {

    DAY_FORMATTER(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    MINUTE_FORMATTER(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
    SECOND_FORMATTER(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    DateTimeFormatter formatter;

    DateFormat(DateTimeFormatter format) {
        this.formatter = format;
    }

    public DateTimeFormatter getFormatter () {
        return formatter;
    }
}
