package no.twomonkeys.sneek.app.shared.helpers;

import android.net.Uri;
import android.util.Log;

import com.facebook.common.file.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.ResponseModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by simenlie on 12.05.16.
 */
public class NetworkHelper {

    private static final String TAG = "NetworkHelper";
    private static String auth_token;
    private static StoryApi networkService;
    //public static StoryApi userService = ServiceGenerator.createService(StoryApi.class, DataHelper.getAuthToken());
    public static StoryApi userService2 = ServiceGenerator.createService(StoryApi.class, null);


    public static void sendRequest(Call<ResponseModel> call, final Contract contract, final MapCallback mcb, final SimpleCallback scb) {
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel responseModel = response.body();
                if (responseModel != null){
                    mcb.callbackCall(contract.generic_contract(responseModel.data));
                }
                handleError(response, scb);
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                Log.v(TAG, "FAILURE");
                t.printStackTrace();
            }
        });
    }

    public static StoryApi getNetworkService()
    {
        String authToken = DataHelper.getAuthToken();
        authToken = "0e8361ed9ea6ea0f6b6715e305e43309";
        if (authToken != auth_token)
        {
            auth_token = authToken;
            networkService = ServiceGenerator.createService(StoryApi.class, authToken);
        }

        return networkService;
    }

    public static void handleError(Response<ResponseModel> response, final SimpleCallback scb) {
        ResponseModel responseModel = response.body();
        ResponseBody errorData = response.errorBody();
        ErrorModel errorModel;

        Log.d(TAG, "onResponse - Status : " + response.code());
        if (response.code() == 401)
        {
            //Log out
           // DataHelper.startActivity.logout();
        }
        Log.d(TAG, "onResponse - BODY : " + response.body());

        if (responseModel != null && responseModel.success) {
            //Success
            Log.v(TAG, "SUCESS " + responseModel.data);
            scb.callbackCall(null);
        } else {
            if (errorData == null) {
                HashMap<String, String> data = new HashMap<>();
                data.put("message_id", "generic_error");
                errorModel = new ErrorModel(DataHelper.getContext(), data);
                scb.callbackCall(errorModel);
            } else {
                try {
                    Map<String, Object> retMap = new Gson().fromJson(errorData.string(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    HashMap<String, Object> withData;
                    if (retMap.get("data") != null) {
                        withData = (HashMap<String, Object>) retMap;
                    } else {
                        withData = new HashMap<>();
                        withData.put("data", retMap);
                    }
                    errorModel = new ErrorModel(DataHelper.getContext(), withData);
                    scb.callbackCall(errorModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void uploadFile(File file, String url, final SimpleCallback scb) {
        // create upload service client
        //already creatd

        // use the FileUtils to get the actual file by uri
//        File file = new File(inputFileUri.getPath());
        Log.v("FILE PATH", "file path is " + file.getAbsolutePath());
        StoryApi service =
                ServiceGenerator2.createService(StoryApi.class);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);


        String uri = Uri.parse(url)
                .buildUpon()
                .build().toString();

        Call<ResponseBody> call = service.upload(uri, requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                scb.callbackCall(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public static void uploadFile2(File file, String url, final SimpleCallback scb, ProgressRequestBody.UploadCallbacks listener) {
        StoryApi service =
                ServiceGenerator2.createService(StoryApi.class);

        ProgressRequestBody prb = new ProgressRequestBody(file, listener);
        RequestBody requestFile = ProgressRequestBody.create(MediaType.parse("image/jpg"), file);

        String uri = Uri.parse(url)
                .buildUpon()
                .build().toString();

        Call<ResponseBody> call = service.upload(uri, prb);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                scb.callbackCall(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

}
