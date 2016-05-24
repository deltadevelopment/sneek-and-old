package no.twomonkeys.sneek.app.shared.helpers;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import no.twomonkeys.sneek.app.shared.models.MomentModel;

/**
 * Created by simenlie on 16.05.16.
 */
public class VideoHelper {

    private int totalRead;
    private boolean started;
    private int mPlayerPosition;
    Activity activity;
    MediaPlayer mediaPlayer;
    VideoView videoView;
    File file;
    MomentModel momentModel;

    public VideoHelper(VideoView videoView, final MomentModel momentModel, Activity activity) {
        this.videoView = videoView;
        this.momentModel = momentModel;
        this.activity = activity;
        //The video helper should determine if the video should be streamed, or player.
        //It should also be responsble for saving the video to disk.
        if (momentModel != null)
        {
            file = momentModel.getVideoFile();
        }
    }

    public void loadVideo() {

        if (momentModel == null)
        {
            File f = new File("/storage/emulated/0/Pictures/MyCameraApp/VID_20160524_130304.mp4");
            videoView.setVideoURI(Uri.fromFile(f));
            videoView.requestFocus();
            videoView.start();
        }
        else{
            if (momentModel.hasCachedVideo()) {
                Log.v("HAS CACHED","CACHED VIDEO");
                File f = new File("/storage/emulated/0/Pictures/MyCameraApp/VID_20160524_130304.mp4");
                videoView.setVideoURI(Uri.fromFile(f));
                videoView.requestFocus();
                videoView.start();
            } else {
                Log.v("HAS NOT CACHED","NO CACHE");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            file.createNewFile();
                            downloadUsingStream(momentModel.getMedia_url(), file);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }

        }


    }

    private void downloadUsingStream(String urlStr, File file) throws IOException {
        URL url = new URL(urlStr);
        Log.v("STart", "is Starting");
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            Log.v("Reading buffer", "Buffering");
            totalRead += count;
            if (totalRead > 200000) {
                if (!started) {
                    Log.v("Starting VIDEO", " STARTED");
                    playFromStream(file);
                    started = true;
                } else {
                    tryStartVideo();
                }
            }

            fis.write(buffer, 0, count);
        }

        fis.close();
        bis.close();
        onCompletion();
    }

    public void onCompletion() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mPlayerPosition = mediaPlayer.getCurrentPosition();
                try {
                    mediaPlayer.reset();
                    videoView.setVideoPath(file.getAbsolutePath());
                    mediaPlayer.seekTo(mPlayerPosition);
                    mediaPlayer.setLooping(true);
                    videoView.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void playFromStream(final File file) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                videoView.setVideoPath(file.getAbsolutePath());
                // videoView.setVideoURI(uri);
                videoView.requestFocus();
            }
        });
    }

    public void tryStartVideo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoView.start();
            }
        });
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
