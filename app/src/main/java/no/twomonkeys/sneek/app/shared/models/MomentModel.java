package no.twomonkeys.sneek.app.shared.models;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import java.io.File;
import java.util.Map;

import no.twomonkeys.sneek.app.shared.SimpleCallback;

/**
 * Created by simenlie on 12.05.16.
 */
public class MomentModel extends CRUDModel {
    public int id, media_type, caption_position, story_id;

    private String created_at, media_key, media_url, thumbnail_url, caption;

    public MomentModel(Map map) {
        build(map);
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


    public void loadPhoto(SimpleDraweeView sdv, final SimpleCallback scb) {
        Log.v("Called", "called");
        Uri uri;
        if (media_type == 0) {
            uri = Uri.parse(media_url);
        } else {
            uri = Uri.parse(thumbnail_url);
        }

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

}
