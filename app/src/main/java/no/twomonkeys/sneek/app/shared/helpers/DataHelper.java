package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.twomonkeys.sneek.app.components.MainActivity;
import no.twomonkeys.sneek.app.components.StartActivity;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 11.05.16.
 */
public class DataHelper {
    public static final String PREFS_NAME = "DefaultPrefs";
    private static Context context;
    public static MainActivity ma;
    public static StartActivity startActivity;
    public static Map<String, String> imageCacheMapHelper;
    public static ArrayList<String> flashSuggestions;
    public static boolean forceUpdateStory;

    public static int currentFeed() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int currentFeedInt = settings.getInt("feedFilter", 0);
        return 0;
    }

    public static void storeCurrentFeed(int row) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("feedFilter", row);

        // Commit the edits!
        editor.commit();
    }

    public static void storeCredentials(String auth_token, int user_id) {
        Log.v("Storing token", "token is " + auth_token);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("auth_token", auth_token);
        editor.putInt("user_id", user_id);
        // Commit the edits!
        editor.commit();
    }

    public static void storeUsername(String username)
    {

    }

    public static String getUsername()
    {
        return "notImplemented";
    }

    public static int getUserId() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        int userId = settings.getInt("user_id", 0);
        return userId;
    }

    public static String getAuthToken() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String authToken = settings.getString("auth_token", "defaultStringIfNothingFound");
        return authToken;
    }

    public static boolean isBlocked(int userId) {
        HashMap<String, Integer> blockedUsers = blockedUsers();
        Log.v("Blocked users", "blocked " + blockedUsers);
        if (blockedUsers != null) {
            Log.v("Blocked users", "blocked " + blockedUsers);
            if (blockedUsers.get(userId + "") != null) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public static void setContext(Context context) {
        DataHelper.context = context;
    }

    public static MainActivity getMa() {
        return ma;
    }

    public static void setMa(MainActivity ma) {
        DataHelper.ma = ma;
    }

    public static Context getContext() {
        return context;
    }

    public static StartActivity getStartActivity() {
        return startActivity;
    }

    public static void setStartActivity(StartActivity startActivity) {
        DataHelper.startActivity = startActivity;
    }

    public static HashMap<String, Integer> blockedUsers() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        Gson gson = new Gson();
        String wrapperStr = settings.getString("blockedUsers", null);
        if (wrapperStr != null) {
            MapWrapper wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
            HashMap<String, Integer> HtKpi = wrapper.getMyMap();
            return HtKpi;
        } else {
            return null;
        }
    }

    public static void blockUser(int userId) {
        HashMap blockedUsers = blockedUsers() == null ? new HashMap() : blockedUsers();
        blockedUsers.put(userId + "", userId);

        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        wrapper.setMyMap(blockedUsers);
        String serializedMap = gson.toJson(wrapper);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("blockedUsers", serializedMap);
        // Commit the edits!
        editor.commit();
    }

    public static void unblockUser(int userId) {
        HashMap blockedUsers = blockedUsers() == null ? new HashMap() : blockedUsers();
        blockedUsers.remove(userId + "");

        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        wrapper.setMyMap(blockedUsers);
        String serializedMap = gson.toJson(wrapper);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("blockedUsers", serializedMap);
        // Commit the edits!
        editor.commit();
    }


    //Tag streams

    public static void moveStreamToFirst(int streamId) {
        String stringStreamId = streamId + "";
        ArrayList tagStreamIdsArray = tagStreamIdsArray();
        tagStreamIdsArray.remove(stringStreamId);
        tagStreamIdsArray.add(0, stringStreamId);
        storeTagStreamIds(tagStreamIdsArray);
    }

    public static ArrayList tagStreamIdsArray() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        Gson gson = new Gson();
        String wrapperStr = settings.getString("tagStreamIdsArray", null);
        if (wrapperStr != null) {
            ArrayListWrapper wrapper = gson.fromJson(wrapperStr, ArrayListWrapper.class);
            return wrapper.getMyList();
        } else {
            return new ArrayList();
        }
    }

    public static void storeTagStreamIds(ArrayList list) {
        Gson gson = new Gson();
        ArrayListWrapper wrapper = new ArrayListWrapper();
        wrapper.setMyList(list);
        String serializedMap = gson.toJson(wrapper);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("tagStreamIdsArray", serializedMap);
        // Commit the edits!
        editor.commit();
    }

    public static void storeTagStream(int streamId, String name) {
        String stringStreamId = streamId + "";
        HashMap<String, String> tagStreams = tagStreams();
        ArrayList<String> tagStreamIdsArray = tagStreamIdsArray();
        boolean stopDoubleStorage = false;

        for (String tagId : tagStreamIdsArray) {
            if (Integer.parseInt(tagId) == streamId) {
                stopDoubleStorage = true;
            }
        }
        String tagStreamName = (String) tagStreams.get(stringStreamId);
        if (tagStreamName == null && streamId != 0) {
            if (!stopDoubleStorage) {
                tagStreamIdsArray.add(stringStreamId);
            }
            tagStreams.put(stringStreamId, name);
            storeTagStreamIds(tagStreamIdsArray);
            storeTagStreams(tagStreams);
        }
    }

    public static ArrayList<String> flashSuggestions() {
        return flashSuggestions;
    }

    public static void addSuggestions(ArrayList<String> list)
    {
        flashSuggestions = list;
    }

    public static void storeTagStreams(HashMap<String, String> map) {
        Gson gson = new Gson();
        MapWrapper2 wrapper = new MapWrapper2();
        wrapper.setMyMap(map);
        String serializedMap = gson.toJson(wrapper);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("tagStreams", serializedMap);
        // Commit the edits!
        editor.commit();
    }

    public static HashMap<String, String> tagStreams() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        Gson gson = new Gson();
        String wrapperStr = settings.getString("tagStreams", null);
        if (wrapperStr != null) {
            MapWrapper2 wrapper = gson.fromJson(wrapperStr, MapWrapper2.class);
            HashMap<String, String> HtKpi = wrapper.getMyMap();
            return HtKpi;
        } else {
            return new HashMap();
        }
    }

    public static boolean shouldForceUpdateStory()
    {
        return forceUpdateStory;
    }

    public static void setForceUpdateStory(boolean shouldForce){
        forceUpdateStory = shouldForce;
    }

    public static void storeEmail(String email){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_email", email);
        editor.commit();
    }

    public static String getEmail()
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String email = settings.getString("user_email", "NA");
        return email;
    }

    public static boolean hasTag(String tagname) {
        Map<String, String> map = tagStreams();
        for (String tagNameSorted : map.values()) {
            if (tagNameSorted.equals(tagname)) {
                return true;
            }
        }
        return false;
    }

    public static String tagId(String tagName) {
        Map<String, String> map = tagStreams();
        for (String tagKey : map.keySet()) {
            String tagNameSorted = map.get(tagKey);
            if (tagNameSorted != null) {
                if (tagNameSorted.equals(tagName)) {
                    return tagKey;
                }
            }
        }
        return null;
    }

    public static void removeTagStream(int streamId) {
        String stringStreamId = streamId + "";
        HashMap<String, String> tagStreams = tagStreams();
        ArrayList<String> tagStreamIdsArray = tagStreamIdsArray();

        tagStreamIdsArray.remove(stringStreamId);
        tagStreams.remove(stringStreamId);

        storeTagStreamIds(tagStreamIdsArray);
        storeTagStreams(tagStreams);
    }

    public static void addCacheHelp(String mediaKey, String url) {
        if (imageCacheMapHelper == null) {
            imageCacheMapHelper = new HashMap<>();
        }
        imageCacheMapHelper.put(url, mediaKey);
    }

    public static Map<String, String> getImageCacheMapHelper() {
        return imageCacheMapHelper;
    }
}
