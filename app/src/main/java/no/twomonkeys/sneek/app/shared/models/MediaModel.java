package no.twomonkeys.sneek.app.shared.models;

import android.graphics.Bitmap;

import java.io.File;

import no.twomonkeys.sneek.app.shared.helpers.MediaHelper;

/**
 * Created by simenlie on 28.05.16.
 */
public class MediaModel {
    private File mediaFile;
    private boolean isSelfie;
    private boolean isVideo;
    //Filter 0, 1, 2
    private int filter;
    private Bitmap bitmapImage;

    public MediaModel(File mediaFile, boolean isSelfie, boolean isVideo) {
        this.mediaFile = mediaFile;
        this.isSelfie = isSelfie;
        this.isVideo = isVideo;
    }

    public File getMediaFile() {
        if (!isVideo){
            if (isSelfie)
            {
                //need to process the image
                return MediaHelper.bitmapToFile(getBitmapImage());
            }
            else
            {
                return mediaFile;
            }
        }
        else{
            return mediaFile;
        }
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
}
