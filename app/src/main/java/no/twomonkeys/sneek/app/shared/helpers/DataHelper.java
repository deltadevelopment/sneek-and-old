package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 11.05.16.
 */
public class DataHelper {
    public static final String PREFS_NAME = "DefaultPrefs";
    private static Context context;

    public static int currentFeed()
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int currentFeedInt = settings.getInt("feedFilter", 0);
        return currentFeedInt;
    }


    public static void storeCurrentFeed(int row)
    {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("feedFilter",row);

        // Commit the edits!
        editor.commit();
    }


    public static boolean isBlocked(StoryModel storyModel)
    {
        return false;
    }

    public static void setContext(Context context) {
        DataHelper.context = context;
    }
}
