package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

/**
 * Created by simenlie on 01.06.16.
 */
public class UserSession extends CRUDModel {
    private UserModel userModel;
    private String auth_token, created_at, updated_at;
    private int user_id;

    @Override
    void build(Map map) {
        if (map.get("user") != null) {
            userModel = new UserModel((Map) map.get("user"));
        }
        auth_token = (String) map.get("auth_token");
        created_at = (String) map.get("created_at");
        updated_at = (String) map.get("updated_at");
        user_id = integerFromObject(map.get("user_id"));
    }

    public UserSession(Map map) {
        build(map);
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
