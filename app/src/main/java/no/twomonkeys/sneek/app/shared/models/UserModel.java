package no.twomonkeys.sneek.app.shared.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 12.05.16.
 */
public class UserModel extends CRUDModel {
    private int id;
    private boolean is_following;
    private String username;
    private int year_born, followers_count;
    private String email;
    private UserSession userSession;
    private StoryModel storyModel;
    private BlockModel blockModel;
    private UserFlagModel userFlagModel;
    private boolean shouldRepeatPassword;
    private String password, passwordAgain;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public interface UserModelCallback {
        void callbackCall(UserModel userModel);
    }

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
        Log.v("HERE WHAT ","what " + map.toString());
        id = integerFromObject(map.get("id"));
        username = (String) map.get("username");
        followers_count = integerFromObject(map.get("followers_count"));
        email = (String) map.get("email");
        is_following = booleanFromObject(map.get("is_following"));
        Log.v("FOLLOWING IS ", "foll" + map.get("is_following") + " " + is_following);
        if (map.get("user_session") != null) {
            Log.v("USer session build", "yeah build");
            userSession = new UserSession((Map) map.get("user_session"));
        }
        if (map.get("story") != null) {
            storyModel = new StoryModel((Map) map.get("story"));
            storyModel.setUser_id(id);
        }
    }

    public void fetch(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getUser(id), GenericContract.v1_get_user(), onDataReturned(), scb);
    }

    public UserSession getUserSession() {
        return userSession;
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

    public void setShouldRepeatPassword(boolean shouldRepeatPassword) {
        this.shouldRepeatPassword = shouldRepeatPassword;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public static void exists(String username, SimpleCallback scb, final UserExistsCallback uec) {
        MapCallback callback = new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                boolean exists = (boolean) map.get("username_exists");
                uec.exists(exists);
            }
        };

        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getUsernameExists(username), GenericContract.v1_get_user_username_exists(), callback, scb);
    }

    public void save(SimpleCallback scb) {
        HashMap innerMap = new HashMap();
        innerMap.put("username", username);
        innerMap.put("password", password);
        innerMap.put("year_born", year_born);
        HashMap<String, HashMap> map = new HashMap();
        map.put("user", innerMap);

        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postUser(map),
                GenericContract.v1_post_user(),
                onDataReturned(),
                scb);
    }

    public void setIs_following(boolean is_following) {
        this.is_following = is_following;
    }

    public BlockModel getBlockModel() {
        if (blockModel == null) {
            blockModel = new BlockModel(id);
        }
        return blockModel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserFlagModel getUserFlagModel() {
        if (userFlagModel == null) {
            userFlagModel = new UserFlagModel(id);
        }
        return userFlagModel;
    }


    public static void fetch(String username, final UserModelCallback umc, SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getUserByUsername(username), GenericContract.v1_get_user_by_username(), new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                UserModel userModel = new UserModel(map);
                umc.callbackCall(userModel);
            }
        }, scb);
    }

    public void delete(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteUser(id),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public String getEmail() {

        return email;
    }

    public void update(final SimpleCallback scb) {
        validate(scb, new SimpleCallback2() {
            @Override
            public void callbackCall() {
                HashMap innerMap = new HashMap();


                if (password != null) {
                    innerMap.put("password", password);
                }
                if (email != null) {
                    innerMap.put("email", email);
                }
                HashMap<String, HashMap> map = new HashMap();
                map.put("user", innerMap);

                NetworkHelper.sendRequest(NetworkHelper.getNetworkService().putUser(map, id),
                        GenericContract.generic_parse(),
                        onDataReturned(),
                        scb);
            }
        });
    }

    //Validation

    private ErrorModel getLocalErrors() {
        ErrorModel errorModel = new ErrorModel(DataHelper.getContext());
        if (shouldRepeatPassword) {
            if (!passwordIsValid()) {
                errorModel.addError("validation_loc_pass", "password");
                errorModel.addError("validation_loc_pass", "passwordAgain");
            }
        }
        if (username != null) {
            if (!emailIsValid()) {
                errorModel.addError("validation_loc_email", "email");
            }
        }
        return errorModel.hasErrors() ? errorModel : null;
    }


    public void validate(SimpleCallback scb, SimpleCallback2 scb2) {
        ErrorModel errors = getLocalErrors();
        if (errors != null) {
            scb.callbackCall(errors);
        } else {
            scb2.callbackCall();
        }
    }

    private boolean passwordIsValid() {
        if (password.equals(passwordAgain)) {
            return true;
        } else {
            return false;
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private boolean emailIsValid() {
        if (email == null) {
            return true;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
