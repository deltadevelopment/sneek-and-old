package no.twomonkeys.sneek.app.shared.models;

import java.util.HashMap;
import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 01.06.16.
 */
public class UserSession extends CRUDModel {
    private UserModel userModel;
    private String auth_token, created_at, updated_at;
    private int user_id;
    boolean isTokenType;

    @Override
    void build(Map map) {
        if (map.get("user") != null) {
            userModel = new UserModel((Map) map.get("user"));
        }
        if (map.get("user_session") != null)
        {
            Map userSessionMap = (Map) map.get("user_session");
            auth_token = (String) userSessionMap.get("auth_token");
            //created_at = (String) userSessionMap.get("created_at");
            //updated_at = (String) userSessionMap.get("updated_at");
            user_id = integerFromObject(userSessionMap.get("user_id"));
        }
        else{

            auth_token = (String) map.get("auth_token");
            //created_at = (String) userSessionMap.get("created_at");
            //updated_at = (String) userSessionMap.get("updated_at");
            user_id = integerFromObject(map.get("user_id"));
        }

    }

    public UserSession(Map map) {
        build(map);
    }

    public UserSession() {

    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void save(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postLogin(asJSON()),
                GenericContract.v1_post_login(),
                onDataReturned(),
                scb);
    }

    public HashMap<String, HashMap> asJSON() {
        HashMap innerMap = new HashMap();
        String usernameEmail = "username";
        if (userModel.getUsername().contains("@")) {
            usernameEmail = "email";
        }
        innerMap.put("device_id", "NA");
        if (isTokenType) {
            innerMap.put("auth_token", "get_token");
        } else {
            innerMap.put("password", userModel.getPassword());
            innerMap.put(usernameEmail, userModel.getUsername());
        }
        HashMap<String, HashMap> map = new HashMap();
        map.put("user", innerMap);

        return map;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getUser_id() {
        return user_id;
    }
}
