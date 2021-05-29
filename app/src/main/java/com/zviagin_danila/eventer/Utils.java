package com.zviagin_danila.eventer;

import android.content.SharedPreferences;
import android.util.Log;

import com.zviagin_danila.eventer.ui.authentication.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Utils {


    public static final String APP_PREFERENCES = "userInfo";
    public static final String APP_PREFERENCES_FIRST_NAME = "firstName";
    public static final String APP_PREFERENCES_LAST_NAME = "lastName";
    public static final String APP_PREFERENCES_BIRTHDAY = "birthday";
    public static final String APP_PREFERENCES_MALE = "male";
    public static final String APP_PREFERENCES_IMAGE_URI = "image";
    public static final String APP_PREFERENCES_CURRENT_ID = "currentId";

    public static String getReadableDateTime(long millisStart, long millisEnd) {
        int dayStart;
        int yearStart;
        int monthStart;
        int hourStart;
        int minuteStart;

        int dayEnd;
        int yearEnd;
        int monthEnd;
        int hourEnd;
        int minuteEnd;

        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
        calendarStart.setTimeInMillis(millisStart);

        Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
        calendarStart.setTimeInMillis(millisEnd);

        dayStart = calendarStart.get(Calendar.DAY_OF_MONTH);
        monthStart = calendarStart.get(Calendar.MONTH);
        yearStart = calendarStart.get(Calendar.YEAR);
        hourStart = calendarStart.get(Calendar.HOUR_OF_DAY);
        minuteStart = calendarStart.get(Calendar.MINUTE);

        dayEnd = calendarEnd.get(Calendar.DAY_OF_MONTH);
        monthEnd = calendarEnd.get(Calendar.MONTH);
        yearEnd = calendarEnd.get(Calendar.YEAR);
        hourEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
        minuteEnd = calendarEnd.get(Calendar.MINUTE);

        return toStringDate(dayStart) + "." + toStringDate(monthStart)
                + " " + hourStart + ":" + minuteStart + " - "
                + toStringDate(dayEnd) + "." + toStringDate(monthEnd)
                + " " + hourEnd + ":" + minuteEnd;
    }

    private static String toStringDate(int date) {
        if (date < 10) {
            return "0" + date;
        } else {
            return String.valueOf(date);
        }
    }

    public static ArrayList<Event> getArrayList(ArrayList<Event> arrayList) {
        if (arrayList == null) {
            return new ArrayList<>();
        } else {
            return arrayList;
        }
    }

    public static Double distance (Double lat_a, Double lng_a, Double lat_b, Double lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Double(distance * meterConversion).doubleValue();
    }
}
