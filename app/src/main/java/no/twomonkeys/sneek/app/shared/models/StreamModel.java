package no.twomonkeys.sneek.app.shared.models;

import android.util.Log;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 13.06.16.
 */
public class StreamModel extends CRUDModel {

    public interface StreamCallback{
        void callbackCall(StreamModel streamModel);
    }

    private int stalkers_count, id;

    public StreamModel() {
        stalkers_count = 0;
    }

    public StreamModel(Map map) {
        build(map);
    }

    @Override
    void build(Map map) {
        Log.v("hel","hello " + map);
        stalkers_count = integerFromObject(map.get("stalkers_count"));
        id = integerFromObject(map.get("id"));
    }

    public static void fetch(String tagName, final StreamCallback stcb, SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getStreamByTag(tagName), GenericContract.v1_get_stream_by_name(), new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                Log.v("hel","hello " + map);
                StreamModel streamModel = new StreamModel(map);
                stcb.callbackCall(streamModel);
            }
        }, scb);
    }

    public int getId() {
        return id;
    }

    public int getStalkers_count() {
        return stalkers_count;
    }

    public void setStalkers_count(int stalkers_count) {
        this.stalkers_count = stalkers_count;
    }
}
