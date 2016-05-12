package no.twomonkeys.sneek.app.shared.APIs;

import no.twomonkeys.sneek.app.shared.models.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;


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
}
