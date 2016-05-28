package no.twomonkeys.sneek.app.components.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        final CameraEditView self = this;

        saveBtn = (Button) findViewById(R.id.cameraEditSaveBtn);

        captionBtn = (Button) findViewById(R.id.cameraEditCaptionBtn);
        backBtn = (Button) findViewById(R.id.cameraEditBackBtn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                self.setVisibility(INVISIBLE);
                //This line makes something strange happen with the top bar, gets black
                //videoView.setVisibility(INVISIBLE);
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
                uploadMedia();
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
    }

    public void loadVideo(File f) {
        videoView.setVisibility(VISIBLE);
        DisplayMetrics dm = new DisplayMetrics();
        DataHelper.getMa().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });
        videoView.setMinimumWidth(width);
        videoView.setMinimumHeight(height);
        videoView.setVideoPath(f.getAbsolutePath());

        videoView.start();
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
        if (!mediaModel.isVideo()){
            //photoTakenView.setImageURI(Uri.fromFile(mediaModel.getMediaFile()));
            photoTakenView.setImageBitmap(mediaModel.getBitmapImage());
            Log.v(TAG,"IMAGE SET");
        }
        else{
            //process video
        }

    }


    public void addMovie(final File f, Context c) {
        isVideo = true;
        final File outFile = getOutputMediaFile(2);

        Log.v("file of out", "out " + outFile.length() / 1024);
        movieFile = f;
        this.c = c;
        media = f;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                test2(f, outFile);
            }
        });
        t.start();
    }

    public void test2(File input, final File output) {
        String s = input.getAbsolutePath();
        String s2 = output.getAbsolutePath();
        FFmpeg ffmpeg = FFmpeg.getInstance(getContext());
        try {
            String[] test2 = new String[]{
                    "-y",                // Overwrite output files
                    "-i",                // Input file
                    s,
                    "-vf",
                    "hue=s=0",
                    "-acodec",           // Audio codec
                    "copy",
                    s2 // Output file
            };
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(test2, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.v("Progress is", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.v("Failure is", message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.v("Message is", message);
                    Log.v("Completed", "COMPLETED NOW");
                    //loadVideo(output);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
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

    public void uploadMedia() {
        uploadBtn.setVisibility(INVISIBLE);
        if (!isVideo) {
            media = saveBitmapToFile(media);
        }

        MomentModel momentModel = new MomentModel();
        momentModel.setMedia_type(isVideo ? 1 : 0);

        //Check if moment on disk is null

        momentModel.setLatitude(0);
        momentModel.setLongitude(0);

        if (captionEditView.hasCaption()) {
            momentModel.setCaption_position(captionPosition);
            momentModel.setCaption(captionEditView.captionTxt());
        }

        momentModel.setMedia(mediaModel.getMediaFile());
        if (isVideo) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(media.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
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
        //setVisibility(INVISIBLE);
        onMediaPosted.callbackCall();
        uploadBtn.setVisibility(VISIBLE);
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
