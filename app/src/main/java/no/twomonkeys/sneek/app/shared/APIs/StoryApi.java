package no.twomonkeys.sneek.app.shared.APIs;

import org.json.JSONObject;

import java.util.HashMap;

import no.twomonkeys.sneek.app.shared.helpers.ProgressRequestBody;
import no.twomonkeys.sneek.app.shared.models.ResponseModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;


/**
 * Created by simenlie on 12.05.16.
 */
public interface StoryApi {
    @GET("user/{user_id}/story")
    Call<ResponseModel> getStory(
            @Path("user_id") int userId
    );

    @GET("feed/{feed}")
    Call<ResponseModel> getFeed(
            @Path("feed") String feed
    );

    @GET("user/username_exists/{username}")
    Call<ResponseModel> getUsernameExists(
            @Path("username") String username
    );

    @POST("moment/generate_upload_url")
    Call<ResponseModel> postGenerateToken(
    );

    @PUT
    @Headers("Content-Type: multipart/form-data;boundary=95416089-b2fd-4eab-9a14-166bb9c5788b")
    Call<ResponseBody> upload(@Url String url,
                              @Body RequestBody body);

    @PUT
    @Headers("Content-Type: multipart/form-data;boundary=95416089-b2fd-4eab-9a14-166bb9c5788b")
    Call<ResponseBody> upload2(@Url String url,
                               @Body ProgressRequestBody file);

    @POST("moment")
    Call<ResponseModel> postMoment(@Body HashMap<String, HashMap> body);

    @POST("user")
    Call<ResponseModel> postUser(@Body HashMap<String, HashMap> body);

}
