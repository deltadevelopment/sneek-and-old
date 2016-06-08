package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 19.05.16.
 */
public class TokenModel extends CRUDModel {
    private String media_url, thumbnail_url, media_key;


    @Override
    void build(Map map) {
        media_url = (String) map.get("media_url");
        thumbnail_url = (String) map.get("thumbnail_url");
        media_key = (String) map.get("media_key");
    }

    void save(final SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postGenerateToken(),
                GenericContract.generate_upload_url(),
                onDataReturned(),
                scb);
    }


    public String getMedia_url() {
        return media_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getMedia_key() {
        return media_key;
    }
}
