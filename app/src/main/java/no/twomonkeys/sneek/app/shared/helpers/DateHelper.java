package no.twomonkeys.sneek.app.shared.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by simenlie on 12.05.16.
 */
public class DateHelper {

    public static boolean hasExpired(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Log.v("date","date " + date);
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
}
