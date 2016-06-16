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
import retrofit2.http.DELETE;
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
    @GET("stream/{stream_id}")
    Call<ResponseModel> getStream(
            @Path("stream_id") int streamId
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

    @PUT("user/{user_id}")
    Call<ResponseModel> putUser(@Body HashMap<String, HashMap> body, @Path("user_id") int user_id);

    @POST("following")
    Call<ResponseModel> postStalkUser(@Body HashMap<String, HashMap> body);

    @POST("login")
    Call<ResponseModel> postLogin(@Body HashMap<String, HashMap> body);

    @DELETE("user/{user_id}/following")
    Call<ResponseModel> deleteStalkUser(
            @Path("user_id") String user_id
    );

    @POST("user/{user_id}/block")
    Call<ResponseModel> postUserBlock(
            @Path("user_id") String user_id
    );
    @DELETE("user/{user_id}/block")
    Call<ResponseModel> deleteUserBlock(
            @Path("user_id") String user_id
    );

    @GET("user/blocked")
    Call<ResponseModel> getBlockedUsers(
    );

    @GET("stalking")
    Call<ResponseModel> getStalkings(
    );

    @POST("stalking/by_name/{stream_name}")
    Call<ResponseModel> postStalkStream(
            @Path("stream_name") String stream_name
    );

    @DELETE("stalking/{stream_id}")
    Call<ResponseModel> deleteStalkStream(
            @Path("stream_id") String stream_id
    );

    @DELETE("moment/{moment_id}")
    Call<ResponseModel> deleteMoment(
            @Path("moment_id") String moment_id
    );

    @DELETE("story/moments")
    Call<ResponseModel> deleteMoments(
    );

    @POST("user/{user_id}/flag")
    Call<ResponseModel> postFlagUser(
            @Path("user_id") String user_id
    );
    @POST("moment/{moment_id}/flag")
    Call<ResponseModel> postFlagMoment(
            @Path("moment_id") String moment_id
    );

    @GET("stream/by_name/{tag_name}")
    Call<ResponseModel> getStreamByTag(
            @Path("tag_name") String tagName
    );
    @GET("user/by_username/{username}")
    Call<ResponseModel> getUserByUsername(
                    @Path("username") String username
            );

    @GET("feed/suggestions")
    Call<ResponseModel> getSuggestions(
    );

    @GET("user/{user_id}")
    Call<ResponseModel> getUser(
            @Path("user_id") int user_id
    );

    @DELETE("user/{user_id}")
    Call<ResponseModel> deleteUser(
            @Path("user_id") int user_id
    );

}
