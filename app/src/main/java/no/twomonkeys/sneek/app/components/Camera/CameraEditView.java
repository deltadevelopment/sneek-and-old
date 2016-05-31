package no.twomonkeys.sneek.app.components.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.facebook.drawee.view.SimpleDraweeView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.sherazkhilji.videffect.DuotoneEffect;
import com.sherazkhilji.videffect.view.VideoSurfaceView;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.MainActivity;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.MediaHelper;
import no.twomonkeys.sneek.app.shared.helpers.ProgressRequestBody;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.helpers.VideoHelper;
import no.twomonkeys.sneek.app.shared.helpers.VideoRenderer;
import no.twomonkeys.sneek.app.shared.helpers.VideoRenderer2;
import no.twomonkeys.sneek.app.shared.models.MediaModel;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.views.CaptionView;
import no.twomonkeys.sneek.app.shared.views.FilterView;
import no.twomonkeys.sneek.app.shared.views.ProgressIndicator;
import no.twomonkeys.sneek.app.shared.views.SneekVideoView;
import retrofit2.http.Url;

/**
 * Created by simenlie on 19.05.16.
 */
public class CameraEditView extends RelativeLayout {

    private static final String TAG = "CameraEditView";
    //SimpleDraweeView photoTakenView;
    Button backBtn, captionBtn, saveBtn, uploadBtn;
    public SimpleCallback onCancelEdit;
    public SimpleCallback onMediaPosted;
    public CaptionEditView captionEditView;
    public CaptionView captionView;
    float oldY;
    boolean isVideo;
    int captionPosition;
    File media;
    View progressView;
    File movieFile;
    VideoHelper videoHelper;
    Context c;
    MainActivity ma;
    SneekVideoView videoView;
    MediaModel mediaModel;
    ImageView photoTakenView;
    TextureView textureVideoView;
    // ScalableVideoView video_view;
    MediaPlayer mPlayer;
    int currentTime;
    FilterView filterView;

    public CameraEditView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CameraEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CameraEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.camera_edit_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        filterView = (FilterView) findViewById(R.id.filterView2);
        filterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TEST","TEST CLICK LA");
                filterView.applyNextFilter(mediaModel.getNextFilter());
            }
        });
        final CameraEditView self = this;
        saveBtn = (Button) findViewById(R.id.cameraEditSaveBtn);
        /*
        video_view = (ScalableVideoView) findViewById(R.id.video_view);
        video_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("CHANGE FILTER", "FILTER CHANGE");
                if (mediaModel.filterComplete()) {
                    try {
                        currentTime = mPlayer.getCurrentPosition();
                        Log.v("CURREN TIME IS", "cur " + currentTime);
                        video_view.stop();
                        video_view.setScalableType(ScalableType.CENTER_TOP_CROP);
                        video_view.invalidate();

                        video_view.setDataSource(mediaModel.getVideoPathForFilter());
                        video_view.prepare(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.seekTo(currentTime);
                                mp.setLooping(true);
                                mPlayer = mp;
                                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                    @Override
                                    public void onSeekComplete(MediaPlayer mp) {
                                        video_view.start();

                                    }
                                });


                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
*/
        captionBtn = (Button) findViewById(R.id.cameraEditCaptionBtn);
        backBtn = (Button) findViewById(R.id.cameraEditBackBtn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //videoView.stopPlayback();
                if (mediaModel.isVideo()) {
                    // video_view.stop();
                    filterView.remove();
                }

                //video_view.setVisibility(INVISIBLE);
                //This line makes something strange happen with the top bar, gets black
                // videoView.setVisibility(View.INVISIBLE);
                //videoView.invalidate();
                //videoView.requestLayout();
                //self.setVisibility(INVISIBLE);

                onCancelEdit.callbackCall();
            }
        });
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMediaToDisk();
            }
        });
        captionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCaption();
            }
        });

        photoTakenView = (ImageView) findViewById(R.id.photoTakenView);
        captionEditView = (CaptionEditView) findViewById(R.id.cameraEditCaptionEdit);
        captionEditView.setVisibility(INVISIBLE);
        uploadBtn = (Button) findViewById(R.id.cameraEditUploadBtn);
        uploadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryUploadMedia();
            }
        });

        captionView = (CaptionView) findViewById(R.id.cameraEditCaptionView);
        captionView.setVisibility(INVISIBLE);
        captionPosition = 0;
        captionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editCaption();
            }
        });


        captionEditView.onCaptionDone = new SimpleCallback() {
            @Override
            public void callbackCall() {
                updateCaption();
            }
        };

        progressView = (View) findViewById(R.id.progressUpload);
        android.app.FragmentManager fragmentManager = DataHelper.getMa().getFragmentManager();

        videoView = (SneekVideoView) findViewById(R.id.videoSneekVideoView3);
        videoView.setZOrderMediaOverlay(true);
        //videoView.setZOrderOnTop(true);


    }

    public void updateCaption() {
        if (captionEditView.hasCaption()) {
            captionView.setVisibility(VISIBLE);
            Layout l = captionEditView.getEditLayout();
            captionView.updateCaption(l.getLineCount(), l.getLineStart(l.getLineCount() - 1), captionEditView.captionTxt());
        }
        showTools();
    }

    public void addPhoto(File file) {
        media = file;
        //photoTakenView.setImageURI(Uri.fromFile(file));
        isVideo = false;
    }

    public void addMedia(MediaModel mediaModel) {
        this.mediaModel = mediaModel;
        filterView.setVisibility(INVISIBLE);
        if (!mediaModel.isVideo()) {
            //photoTakenView.setImageURI(Uri.fromFile(mediaModel.getMediaFile()));
            photoTakenView.setImageBitmap(mediaModel.getBitmapImage());
            Log.v(TAG, "IMAGE SET");
        } else {

            filterView.setVisibility(VISIBLE);
            filterView.setVideo(mediaModel.getMediaFile().getAbsolutePath(), getContext(), mediaModel.isSelfie());
            /*
            mediaModel.processFilters(getContext());
            video_view.setVisibility(VISIBLE);

            //process video
            // videoView.setScaleX(-1);
            // loadVideo(mediaModel.getMediaFile());
            try {
                if (mediaModel.isSelfie()) {
                    video_view.setScaleX(-1);
                } else {
                    video_view.setScaleX(1);
                }
                video_view.setDataSource(mediaModel.getMediaFile().getAbsolutePath());

                video_view.setScalableType(ScalableType.CENTER_TOP_CROP);
                video_view.invalidate();
                // video_view.start();
                video_view.prepare(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mPlayer = mp;
                        mp.setLooping(true);
                        video_view.start();
                    }
                });
                video_view.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        Log.v("WHAT", "WHAT");
                        return false;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

        }

    }



    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void saveMediaToDisk() {

    }

    public void addCaption() {
        if (captionEditView.hasCaption()) {
            //Move around
            switch (captionPosition) {
                case 0: {
                    oldY = captionView.getY();
                    moveCaptionToY(UIHelper.screenHeight(getContext()) / 2);
                    captionPosition = 1;
                    break;
                }
                case 1: {
                    moveCaptionToY(UIHelper.screenHeight(getContext()) - 150);
                    captionPosition = 2;
                    break;
                }
                case 2: {
                    moveCaptionToY(oldY);
                    captionPosition = 0;
                    break;
                }
            }
        } else {
            //start adding caption
            editCaption();
        }
    }

    public void compressImage() {


        //create a file to write bitmap data
        File f = new File(getContext().getCacheDir(), "tmp.jpeg");
        try {
            f.createNewFile();
            //Convert bitmap to byte array
            Bitmap bitmap = BitmapFactory.decodeFile(media.getAbsolutePath());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            media = f;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public void tryUploadMedia() {
        //Obtain the file, processed and clear
        if (mediaModel.isVideo()) {
            // video_view.stop();
            filterView.stop();
        }
        mediaModel.videoProcessedCallback = new MediaModel.VideoProcessedCallback() {
            @Override
            public void onProcessed(File file) {
                uploadMedia(file);
            }
        };
        mediaModel.processMedia(getContext());
    }

    public void uploadMedia(File file) {
        uploadBtn.setVisibility(INVISIBLE);

        MomentModel momentModel = new MomentModel();
        momentModel.setMedia_type(mediaModel.isVideo() ? 1 : 0);

        //Check if moment on disk is null

        momentModel.setLatitude(0);
        momentModel.setLongitude(0);

        if (captionEditView.hasCaption()) {
            momentModel.setCaption_position(captionPosition);
            momentModel.setCaption(captionEditView.captionTxt());
        }
        momentModel.setMedia(file);
        //momentModel.setMedia(mediaModel.getMediaFile());
        if (mediaModel.isVideo()) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mediaModel.getMediaFile().getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            File f = getOutputMediaFile(1);
            try {
                f.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(f);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                momentModel.setThumbnail(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //set disk file path?


        }
        // momentModel setplaceId

        momentModel.progress = new MomentModel.MomentCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                float percentageOfScreen = (UIHelper.screenWidth(getContext()) * percentage) / 100;

                LayoutParams lr = new LayoutParams(0, UIHelper.dpToPx(getContext(), 2));
                lr.width = (int) percentageOfScreen;
                progressView.setLayoutParams(lr);
            }
        };

        momentModel.saveWithProgression(new SimpleCallback() {
            @Override
            public void callbackCall() {
                Log.v("POSTED TO BEN", "POSTED MOMENT");
                onMediaPosted();
            }
        });
    }

    public void onMediaPosted() {
        LayoutParams lr = new LayoutParams(0, UIHelper.dpToPx(getContext(), 2));
        lr.width = 0;
        progressView.setLayoutParams(lr);
        setVisibility(INVISIBLE);
        onMediaPosted.callbackCall();
        uploadBtn.setVisibility(VISIBLE);
        filterView.remove();
    }

    public void moveCaptionToY(float yPos) {
        captionView.setY(yPos);
    }

    public void editCaption() {
        captionEditView.setVisibility(VISIBLE);
        captionEditView.startCaption();
        hideTools();
    }

    public void hideTools() {
        backBtn.setVisibility(INVISIBLE);
        uploadBtn.setVisibility(INVISIBLE);
        saveBtn.setVisibility(INVISIBLE);
        captionBtn.setVisibility(INVISIBLE);
        captionView.setVisibility(INVISIBLE);
    }

    public void showTools() {
        backBtn.setVisibility(VISIBLE);
        uploadBtn.setVisibility(VISIBLE);
        saveBtn.setVisibility(VISIBLE);
        captionBtn.setVisibility(VISIBLE);
    }

}
