package no.twomonkeys.sneek.app.shared.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 08.06.16.
 */
public class StalkModel extends CRUDModel {

    private int user_id, followee_id, stream_id;
    private String tag_name;

    @Override
    void build(Map map) {
        if (map.get("stalking") != null) {
            map = (Map) map.get("stalking");
        }

        stream_id = integerFromObject(map.get("stream_id"));
    }

    public void saveUser(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postStalkUser(asJSON()),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public void deleteUser(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteStalkUser(followee_id + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public void saveStream(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postStalkStream(tag_name),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public void deleteStream(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteStalkStream(stream_id + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public HashMap<String, HashMap> asJSON() {
        HashMap innerMap = new HashMap();

        innerMap.put("user_id", user_id);
        innerMap.put("followee_id", followee_id);

        HashMap<String, HashMap> map = new HashMap();
        map.put("following", innerMap);

        return map;
    }

    public static void fetchAll(final SimpleCallback scb) {
        final ArrayList blockedUsers = new ArrayList();

        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getStalkings(),
                GenericContract.generic_parse(),
                new MapCallback() {
                    @Override
                    public void callbackCall(Map map) {
                        if (map.get("stalkings") != null) {
                            ArrayList stalkingsArray = (ArrayList) map.get("stalkings");
                            ArrayList tagsStreamIdsArray = DataHelper.tagStreamIdsArray();
                            HashMap<String, String> tagStreams = new HashMap<>();
                            for (Object streamStalking : stalkingsArray) {
                                Map streamStalkingMap = (Map) streamStalking;
                                Double doubleValue = (Double) streamStalkingMap.get("id");
                                String stringStreamId = doubleValue.intValue() + "";
                                String streamName = (String) streamStalkingMap.get("name");
                                if (!isInside(stringStreamId)) {
                                    tagsStreamIdsArray.add(stringStreamId);
                                }
                                tagStreams.put(stringStreamId, streamName);
                            }

                            DataHelper.storeTagStreamIds(tagsStreamIdsArray);
                            DataHelper.storeTagStreams(tagStreams);
                        }
                    }
                },
                scb);
    }


    public static boolean isInside(String stringID) {
        ArrayList<String> oldTagIds = DataHelper.tagStreamIdsArray();
        boolean isInside = false;
        for (String s : oldTagIds) {
            int intValue = Integer.parseInt(s);
            int stringIdInt = Integer.parseInt(stringID);

            if (intValue == stringIdInt) {
                isInside = true;
                break;
            }
        }
        return isInside;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFollowee_id() {
        return followee_id;
    }

    public void setFollowee_id(int followee_id) {
        this.followee_id = followee_id;
    }

    public int getStream_id() {
        return stream_id;
    }

    public void setStream_id(int stream_id) {
        this.stream_id = stream_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }
}
