package no.twomonkeys.sneek.app.shared.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import no.twomonkeys.sneek.app.shared.MapCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;
import no.twomonkeys.sneek.app.shared.helpers.ProgressRequestBody;
import retrofit2.http.Url;

/**
 * Created by simenlie on 12.05.16.
 */
public class MomentModel extends CRUDModel implements ProgressRequestBody.UploadCallbacks {
    public int id, media_type, caption_position, story_id, place_id;
    private float latitude, longitude;

    private String created_at, media_key, media_url, thumbnail_url, caption;
    private File media, thumbnail;
    private UserModel userModel;
    private MomentFlagModel momentFlagModel;

    public interface MomentCallbacks {
        void onProgressUpdate(int percentage);
    }

    public MomentCallbacks progress;

    TokenModel tokenModel;
    float overAllProgress;
    boolean isUploadingThumbnail;


    public MomentModel(Map map) {
        build(map);
    }


    public MomentModel() {
        tokenModel = new TokenModel();
    }

    public void build(Map map) {
        created_at = (String) map.get("created_at");
        media_key = (String) map.get("media_key");
        media_url = (String) map.get("media_url");
        thumbnail_url = (String) map.get("thumbnail_url");
        caption = (String) map.get("caption");
        id = integerFromObject(map.get("id"));
        media_type = integerFromObject(map.get("media_type"));
        caption_position = integerFromObject(map.get("caption_position"));
        story_id = integerFromObject(map.get("story_id"));
        if (map.get("user") != null) {
            userModel = new UserModel((Map) map.get("user"));
        }
    }

    public String getMedia_key() {
        return media_key;
    }

    public String getMedia_url() {
        return media_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getCaption() {
        return caption;
    }

    public String getCreated_at() {
        return created_at;
    }


    public void loadPhoto(SimpleDraweeView sdv, final SimpleCallback2 scb) {
        Log.v("Called", "called");


        Uri uri;
        if (media_type == 0) {
            DataHelper.addCacheHelp(media_key, media_url);
            uri = Uri.parse(media_url);
        } else {
            DataHelper.addCacheHelp(media_key, thumbnail_url);
            uri = Uri.parse(thumbnail_url);
        }

       // sdv.setImageURI(uri);
       // isIndisk(uri);
       Log.v("IS CACHED?","disk " + isDownloaded(uri));
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {

                scb.callbackCall();
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                // FLog.d("Intermediate image received")
                Log.v("Img Re", "Recieved");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
            }
        };

        Log.v("URI IS", "uri " + uri);


        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(sdv.getController())
                .setControllerListener(controllerListener)
                .build();
        sdv.setController(controller);

    }

    private boolean isDownloaded(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest);

        Log.v("Cache key","key is " + cacheKey.toString() + " : " + media_key);
        SimpleCacheKey simpleCacheKey = new SimpleCacheKey(media_key);
        return ImagePipelineFactory.getInstance()
                .getMainDiskStorageCache().hasKey(simpleCacheKey);
    }

    public void isIndisk(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<Boolean> inDiskCacheSource = imagePipeline.isInDiskCache(uri);
        DataSubscriber<Boolean> subscriber = new BaseDataSubscriber<Boolean>() {
            @Override
            protected void onNewResultImpl(DataSource<Boolean> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }
                boolean isInCache = dataSource.getResult();
                Log.v("success", "is in cache" + isInCache);
                // your code here
            }

            @Override
            protected void onFailureImpl(DataSource<Boolean> dataSource) {
                Log.v("failure", " failure");
            }
        };
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                Log.v("executor ", "executor done ");
            }
        };
        inDiskCacheSource.subscribe(subscriber, executor);
    }


    public boolean hasCachedVideo() {
        String filename = media_key + ".mp4";

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        File file = new File(path, "/" + filename);

        if (file.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public File getVideoFile() {
        String filename = media_key + ".mp4";

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        File file = new File(path, "/" + filename);
        return file;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public void setCaption_position(int caption_position) {
        this.caption_position = caption_position;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setMedia_key(String media_key) {
        this.media_key = media_key;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public void upload(File file, String url, SimpleCallback scb) {

        NetworkHelper.uploadFile2(file, url, scb, this);
        //NetworkHelper.uploadFile(file, url, scb);
    }


    public void saveWithProgression(final SimpleCallback scb) {
        //Generate upload url
        tokenModel.save(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                //Start uploading the actual media
                uploadMedia(scb);
            }
        });
    }


    public void uploadMedia(final SimpleCallback scb) {
        setMedia_key(tokenModel.getMedia_key());
        Log.v("TOKENOMDEOl", "tokenModel " + tokenModel.getMedia_url());
        upload(media, tokenModel.getMedia_url(), new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                //Upload was successful
                //Store to BEN
                putMoment(scb);
                //scb.callbackCall();
            }
        });
    }


    public void putMoment(final SimpleCallback scb) {
        //check if thumbnail needs to be uploaded
        if (media_type == 1) {
            isUploadingThumbnail = true;
            upload(thumbnail, tokenModel.getThumbnail_url(), new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    //done uploading thumbnail
                    save(scb);
                }
            });
        } else {
            save(scb);
        }
    }

    public void save(SimpleCallback scb) {
        HashMap innerMap = new HashMap();
        innerMap.put("media_key", media_key);
        innerMap.put("media_type", media_type);
        innerMap.put("caption", caption == null ? "" : caption);
        innerMap.put("caption_position", caption_position);
        innerMap.put("longitude", longitude);
        innerMap.put("latitude", latitude);
        innerMap.put("place_id", place_id);

        HashMap<String, HashMap> map = new HashMap();
        map.put("moment", innerMap);

        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().postMoment(map),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public void delete(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteMoment(id + ""),
                GenericContract.generic_parse(),
                onDataReturned(),
                scb);
    }

    public static void deleteAll(SimpleCallback scb) {
        NetworkHelper.sendRequest(NetworkHelper.getNetworkService().deleteMoments(),
                GenericContract.generic_parse(),
                new MapCallback() {
                    @Override
                    public void callbackCall(Map map) {

                    }
                },
                scb);
    }

    public void setMedia(File media) {
        this.media = media;
    }

    public void setThumbnail(File thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public void onProgressUpdate(int percentage) {
        if (media_type == 1) {
            if (!isUploadingThumbnail) {
                overAllProgress = (percentage / 2);
            } else {
                overAllProgress += (percentage / 2);
            }
        } else {
            overAllProgress = percentage;
        }
        Log.v("Percent", "perc " + percentage);
        progress.onProgressUpdate((int) overAllProgress);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    public UserModel getUserModel() {
        return userModel;
    }

    public MomentFlagModel getMomentFlagModel() {
        if (momentFlagModel == null){
            momentFlagModel = new MomentFlagModel(id);
        }
        return momentFlagModel;
    }
}
