package com.jxust.qq.superquestionlib.util.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateFormat
{
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date DateFormatParse(String date_str)
    {
        Date date=null;
        try {
            date=sdf.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static Date parseUTCText(Date or_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text=sdf.format(or_date);
        if (text.indexOf(".") > -1) {
            String prefix = text.substring(0, text.indexOf("."));
            String suffix = text.substring(text.indexOf("."));
            if (suffix.length() >= 5) {
                suffix = suffix.substring(0, 4) + "Z";
            } else {
                int len = 5 - suffix.length();
                String temp = "";
                temp += suffix.substring(0, suffix.length() - 1);
                for (int i = 0; i < len; i++) {
                    temp += "0";
                }
                suffix = temp + "Z";
            }
            text = prefix + suffix;
        } else {
            text = text.substring(0, text.length() - 1) + ".000Z";
        }
        Date date = sdf.parse(text);
        return date;
    }
    public static Map<String,Date> getTime(String period) {

        Map<String,Date> timeRange = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        timeRange.put("endTime",new Date((calendar.getTime().getTime())));
        switch (period) {

            case "week": {
                calendar.add(Calendar.DATE, -7);
            }
            break;

            case "month": {
                calendar.add(Calendar.MONTH, -1);
            }
            break;

            default: break;

        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeRange.put("startTime",calendar.getTime());
        return timeRange;
    }
}
