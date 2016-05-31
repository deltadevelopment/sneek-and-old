package no.twomonkeys.sneek.app.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

import no.twomonkeys.sneek.app.shared.helpers.MediaHelper;

/**
 * Created by simenlie on 28.05.16.
 */
public class MediaModel {
    private File mediaFile;
    public File bwFilterFile;
    public File vintageFile;
    private boolean isSelfie;
    private boolean isVideo;
    //Filter 0, 1, 2
    private int filter;
    private Bitmap bitmapImage;
    private boolean isFiltered;
    private boolean isFiltered2;
    private int currentFilter;

    public interface VideoProcessedCallback {
        void onProcessed(File file);
    }

    public VideoProcessedCallback videoProcessedCallback;


    public MediaModel(final File mediaFile, boolean isSelfie, boolean isVideo) {
        this.mediaFile = mediaFile;
        this.isSelfie = isSelfie;
        this.isVideo = isVideo;
        final MediaModel self = this;
        MediaHelper.videoMirrored = new MediaHelper.VideoMirroredCallback() {
            @Override
            public void onMirrored(File file) {
                self.mediaFile = file;
                videoProcessedCallback.onProcessed(self.mediaFile);
            }
        };
        MediaHelper.videoProcessed = new MediaHelper.VideoProcessedCallback() {
            @Override
            public void onProcessed(File file) {
                self.mediaFile = file;
                videoProcessedCallback.onProcessed(self.mediaFile);
            }
        };
    }

    //this method processes the media and calls a callback
    public void processMedia(Context context) {
        if (!isVideo) {
            if (isSelfie) {
                //need to process the image
                File f = MediaHelper.bitmapToFile(getBitmapImage());
                videoProcessedCallback.onProcessed(f);
            } else {
                videoProcessedCallback.onProcessed(mediaFile);
            }
        } else {
            MediaHelper.processVideo(mediaFile, isSelfie(), currentFilter, context);
        }
    }


    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
    }

    public boolean isSelfie() {
        return isSelfie;
    }

    public void setSelfie(boolean selfie) {
        isSelfie = selfie;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public Bitmap getBitmapImage() {
        if (bitmapImage == null) {
            if (isSelfie) {
                bitmapImage = MediaHelper.mirrorImage(mediaFile);
            } else {
                bitmapImage = MediaHelper.bitmapImage(mediaFile);
            }
        }
        return bitmapImage;
    }

    public String getVideoPathForFilter() {
        switch (currentFilter) {
            case 0: {
                currentFilter = 1;
                return bwFilterFile.getAbsolutePath();
            }
            case 1: {
                currentFilter = 2;
                return vintageFile.getAbsolutePath();
            }
            case 2: {
                currentFilter = 0;
                return mediaFile.getAbsolutePath();
            }
        }
        return null;
    }

    public int getNextFilter() {
        currentFilter++;
        if (currentFilter == 3) {
            currentFilter = 0;
        }
        return currentFilter;
    }


    public void processFilters(Context context) {
        MediaHelper.bwFiltered = new MediaHelper.VideoFilterCallback() {
            @Override
            public void onFiltered(File file) {
                bwFilterFile = file;
                isFiltered = true;
            }
        };
        MediaHelper.vintageFiltered = new MediaHelper.VideoFilterCallback() {
            @Override
            public void onFiltered(File file) {
                vintageFile = file;
                isFiltered2 = true;
            }
        };
        MediaHelper.blackWhiteVideo(mediaFile, context);
        MediaHelper.vintageVideo(mediaFile, context);
    }

    public boolean filterComplete() {
        if (isFiltered && isFiltered2) {
            return true;
        } else {
            return false;
        }
    }

    public void clean() {
        //This method should remove files that was stored temporary etc
    }

}
