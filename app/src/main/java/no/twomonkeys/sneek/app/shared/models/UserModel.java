package no.twomonkeys.sneek.app.shared.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 12.05.16.
 */
public class UserModel extends CRUDModel {
    private int id;
    private boolean is_following;
    private String username, password;
    private int year_born;
    private UserSession userSession;
    private StoryModel storyModel;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getYear_born() {
        return year_born;
    }

    public void setYear_born(int year_born) {
        this.year_born = year_born;
    }

    public UserModel(Map map) {
        build(map);
    }

    public UserModel() {

    }

    public void build(Map map) {
        id = integerFromObject(map.get("id"));
        username = (String) map.get("username");

        if (map.get("user_session") != null) {
            userSession = new UserSession((Map) map.get("user_session"));
        }
        if (map.get("story") != null) {
            storyModel = new StoryModel((Map) map.get("story"));
            storyModel.setUser_id(id);
        }
    }

    public interface UserExistsCallback {
        public void exists(boolean exists);
    }

    public int getId() {
        return id;
    }

    public boolean is_following() {
        return is_following;
    }

    public String getUsername() {
        return username;
    }


    public static void exists(String username, SimpleCallback scb, final UserExistsCallback uec) {
        MapCallback callback = new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                boolean exists = (boolean) map.get("username_exists");
                uec.exists(exists);
            }
        };

        NetworkHelper.sendRequest(NetworkHelper.userService.getUsernameExists(username), GenericContract.v1_get_user_username_exists(), callback, scb);
    }

    public void save(SimpleCallback scb) {
        HashMap innerMap = new HashMap();
        innerMap.put("username", username);
        innerMap.put("password", password);
        innerMap.put("year_born", year_born);
        HashMap<String, HashMap> map = new HashMap();
        map.put("user", innerMap);

        NetworkHelper.sendRequest(NetworkHelper.userService.postUser(map),
                GenericContract.v1_post_user(),
                onDataReturned(),
                scb);
    }
}
