package no.twomonkeys.sneek.app.shared.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by simenlie on 12.05.16.
 */
public class DateHelper {

    public static boolean hasExpired(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Log.v("date", "date " + date);
            Date date1 = formatter.parse(date);

            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(date1); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 24); // adds one hour
            date1 = cal.getTime();

            Date date2 = new Date();

            if (date1.compareTo(date2) < 0) {
                return true;
            }

        } catch (ParseException e) {
            return false;
        }

        return false;
    }


    public static String shortTimeSince(String date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.v("date", "date " + date);
        try {
            Date date1 = formatter.parse(date);
            String outputString;

            long diff = new Date().getTime() - date1.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds > 60) {
                if (minutes > 60) {
                    if (hours > 24) {
                        outputString = days + "d";
                    } else {
                        outputString = hours + "h";
                    }
                } else {
                    outputString = minutes + "m";
                }
            } else {
                outputString = seconds + "s";
            }

            return outputString;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
