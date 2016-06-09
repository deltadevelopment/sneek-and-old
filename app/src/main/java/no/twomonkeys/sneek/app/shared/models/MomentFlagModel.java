package no.twomonkeys.sneek.app.shared.models;

import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 10.06.16.
 */
public class MomentFlagModel extends CRUDModel {

    int moment_id;

    @Override
    void build(Map map) {

    }

    public MomentFlagModel(int moment_id) {
        this.moment_id = moment_id;
    }

    public void save(SimpleCallback scb)
    {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postFlagMoment(moment_id + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }
}
