package no.twomonkeys.sneek.app.shared.models;

import java.util.ArrayList;
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
public class BlockModel extends CRUDModel {

    private int userId;

    public interface ArrayCallback
    {
        void callbackCall(ArrayList arrayList);
    }

    public BlockModel(int userId) {
        this.userId = userId;
    }

    @Override
    void build(Map map) {

    }

    public void save(SimpleCallback scb) {
        DataHelper.blockUser(userId);
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postUserBlock(userId + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public void delete(SimpleCallback scb) {
        DataHelper.unblockUser(userId);
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteUserBlock(userId + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public static void fetchAll(final ArrayCallback scb) {
        final ArrayList blockedUsers = new ArrayList();

        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().getBlockedUsers(),
                GenericContract.generic_parse(),
                new MapCallback() {
                    @Override
                    public void callbackCall(Map map) {
                        ArrayList blockedUsersRaw = (ArrayList) map.get("blocked_users");
                        for (Object rawUser : blockedUsersRaw) {
                            UserModel userModel = new UserModel((Map) rawUser);
                            DataHelper.blockUser(userModel.getId());
                            blockedUsers.add(userModel);
                        }
                    }
                },
                new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        if (errorModel == null){
                            scb.callbackCall(blockedUsers);
                        }
                    }
                });
    }
}
