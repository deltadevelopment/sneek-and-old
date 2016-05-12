package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

/**
 * Created by simenlie on 12.05.16.
 */
public class UserModel extends CRUDModel {
    private int id;
    private boolean is_following;
    private String username;

    public UserModel(Map map) {
        build(map);
    }

    public void build(Map map) {
        id = integerFromObject(map.get("id"));
        username = (String) map.get("username");
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
}
