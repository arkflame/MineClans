package com.arkflame.modernlib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private DateUtils() {
        // Hide constructor
    }
    
    public static String getCurrentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
