package no.twomonkeys.sneek.app.shared.models;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.Contract;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;
import no.twomonkeys.sneek.app.shared.helpers.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by simenlie on 12.05.16.
 */
public class StoryModel extends CRUDModel {

    private static final String TAG = "StoryModel";
    private Rect bigFrame;
    private Rect frame;
    private Point cellSize;
    private Point bigCellSize;
    public int id, user_id, moments_count;
    public boolean is_following;
    private String created_at, updated_at;
    private ArrayList<MomentModel> moments;
    private UserModel userModel;


    public StoryModel(Map storyRaw) {
        build(storyRaw);
    }

    public StoryModel() {

    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public ArrayList<MomentModel> getMoments() {
        return moments;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void build(Map map) {
        Log.v(TAG, "Building " + map);
        moments = new ArrayList<MomentModel>();
        id = integerFromObject(map.get("id"));
        user_id = integerFromObject(map.get("user_id"));
        moments_count = integerFromObject(map.get("moments_count"));
        created_at = (String) map.get("created_at");
        updated_at = (String) map.get("updated_at");

        if (map.get("user") != null) {
            userModel = new UserModel((Map)map.get("user"));
            user_id = userModel.getId();
            is_following = userModel.is_following();
        }

        if (map.get("moments") != null) {
            ArrayList<Map> momentsRaw = (ArrayList) map.get("moments");
            for (Map momentRaw : momentsRaw) {
                MomentModel momentModel = new MomentModel(momentRaw);
                if (moments.size() > 0) {
                    if (momentModel.id != moments.get(0).id) {
                        moments.add(0, momentModel);
                    }
                } else {
                    moments.add(0, momentModel);
                }
            }
        }

        if (map.get("moment") != null) {
            MomentModel momentModel = new MomentModel((Map)map.get("moment"));
            moments.add(momentModel);
        }
    }

    public MomentModel getCurrentMoment()
    {
        if (moments.size() > 0)
        {
            return moments.get(moments.size() - 1);
        }
        return null;
    }
    public void fetch(final SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.userService.getStory(user_id), GenericContract.get_story(), onDataReturned(), scb);
    }

    //Setters

    public void setBigCellSize(Point bigCellSize) {
        this.bigCellSize = bigCellSize;
    }

    public void setBigFrame(Rect bigFrame) {
        this.bigFrame = bigFrame;
    }

    public void setFrame(Rect frame) {
        this.frame = frame;
    }

    public void setCellSize(Point cellSize) {
        this.cellSize = cellSize;
    }

    public Rect getBigFrame() {
        return bigFrame;
    }

    public Rect getFrame() {
        return frame;
    }

    public Point getCellSize() {
        return cellSize;
    }

    public Point getBigCellSize() {
        return bigCellSize;
    }
}
