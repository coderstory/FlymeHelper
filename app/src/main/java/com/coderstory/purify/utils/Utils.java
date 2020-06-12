package com.coderstory.purify.utils;

import android.content.Context;

import com.coderstory.purify.config.Misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private static long compareDay(String day1, String day2) {
        try {
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(day1);
            Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(day2);
            return (d2.getTime() - d1.getTime()) / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean vi() {
        return vp() >= 0;
    }

    public static long vp() {
        return compareDay(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), Misc.endTime);
    }
}
