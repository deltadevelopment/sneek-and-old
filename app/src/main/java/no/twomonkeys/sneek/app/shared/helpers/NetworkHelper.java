package no.twomonkeys.sneek.app.shared.helpers;

import android.net.Uri;
import android.util.Log;

import com.facebook.common.file.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import no.twomonkeys.sneek.app.shared.APIs.StoryApi;
import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.models.ResponseModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by simenlie on 12.05.16.
 */
public class NetworkHelper {

    private static final String TAG = "NetworkHelper";
    public static StoryApi userService = ServiceGenerator.createService(StoryApi.class, "36b5c17ccf6f7a16b23f7a0b469f7fc0");
    public static StoryApi userService2 = ServiceGenerator.createService(StoryApi.class, null);

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
                scb.callbackCall();
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
                scb.callbackCall();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

}
