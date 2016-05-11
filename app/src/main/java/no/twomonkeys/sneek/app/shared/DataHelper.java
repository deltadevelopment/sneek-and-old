package no.twomonkeys.sneek.app.shared;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by simenlie on 11.05.16.
 */
public class DataHelper {
    public static final String PREFS_NAME = "DefaultPrefs";

    public static int currentFeed(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int currentFeedInt = settings.getInt("feedFilter", 0);
        return currentFeedInt;
    }


    public static void storeCurrentFeed(Context context, int row)
    {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("feedFilter",row);

        // Commit the edits!
        editor.commit();
    }
}
