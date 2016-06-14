package no.twomonkeys.sneek.app.shared.models;

import java.util.ArrayList;
import java.util.Map;

import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 14.06.16.
 */
public class SuggestionsModel extends CRUDModel {

    public interface SuggestionsCallback{
       void callbackCall(ArrayList<String> suggestions);
    }

    @Override
    void build(Map map) {

    }

    public static void fetchAll(final SuggestionsCallback ssc ,SimpleCallback scb)
    {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getSuggestions(), GenericContract.generic_parse(), new MapCallback() {
            @Override
            public void callbackCall(Map map) {
                ArrayList<String> list = (ArrayList<String>) map.get("suggestions");
                ssc.callbackCall(list);
            }
        }, scb);
    }

}
