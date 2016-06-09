package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 10.06.16.
 */
public class UserFlagModel extends CRUDModel {
    int user_id;
    @Override
    void build(Map map) {

    }

    public UserFlagModel(int user_id) {
        this.user_id = user_id;
    }

    public void save(SimpleCallback scb)
    {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postFlagUser(user_id + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

}
