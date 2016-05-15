package no.twomonkeys.sneek.app.shared.helpers;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.Callable;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.models.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by simenlie on 12.05.16.
 */
public class NetworkHelper {

    private static final String TAG = "NetworkHelper";
    public static StoryApi userService = ServiceGenerator.createService(StoryApi.class, "f68022102ad2e2335904a5221f4e2a8d");


    public static void sendRequest(Call<ResponseModel> call, final Contract contract, final MapCallback mcb, final SimpleCallback scb) {
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel responseModel = response.body();
                Log.d(TAG, "onResponse - Status : " + response.code());

                if (responseModel != null) {
                    Log.v(TAG, "SUCESS " + responseModel.data);
                    mcb.callbackCall(contract.generic_contract(responseModel.data));
                    scb.callbackCall();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                Log.v(TAG, "FAILURE");
                t.printStackTrace();
            }
        });
    }

}
