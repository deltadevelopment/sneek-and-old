package no.twomonkeys.sneek.app.components.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.helpers.VideoRenderer2;
import no.twomonkeys.sneek.app.shared.models.MediaModel;

/**
 * Created by simenlie on 11.05.16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder mHolder;
    public Camera mCamera;
    private static final String TAG = "CameraPreview";
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    int width, height;
    int cameraId;
    boolean flashIsOn, selfieIsOn;
    MediaRecorder mediaRecorder;
    File recordedFile;
    Activity parentActivity;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    boolean isFrontFacing;
    VideoRenderer2 vr2;
    boolean isRecording;
    boolean completed;
    int imageFormat;
    boolean hideButNotKill;

    //Callbacks
    public interface PhotoTakenCallback {
        void onTaken(MediaModel mediaModel);
    }

    public interface VideoRecordedCallback {
        void onRecorded(MediaModel mediaModel);
    }

    public interface VideoRecordingStartedCallback {
        void onRecording();
    }

    public VideoRecordedCallback videoRecordedCallback;
    public VideoRecordingStartedCallback videoRecordingStartedCallback;
    public PhotoTakenCallback photoTakenCallback;

    public CameraPreview(Context context, Camera camera, Activity activity) {
        super(context);
        mCamera = camera;
        if (mCamera != null) {
            this.parentActivity = activity;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mHolder = getHolder();
            mHolder.addCallback(this);
            // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    public void hide(boolean notShouldKill) {
        hideButNotKill = notShouldKill;
        setAlpha(hideButNotKill ? 0.0f : 1.0f);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (holder.equals(this.getHolder())) {
            Log.v("NOT CONF", "NOT CONF");
        } else {
            Log.v("CONFLICT", "CONLFIT");
        }
        //holder.setFormat(PixelFormat.TRANSPARENT);
        final SurfaceHolder s = holder;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "surface created");
                // The Surface has been created, now tell the camera where to draw the preview.
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(s);
                        mCamera.startPreview();
                    }
                    completed = true;

                } catch (IOException e) {
                    Log.d(TAG, "Error setting camera preview: " + e.getMessage());
                }
            }
        });
        t.start();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void test() {
        mHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.v(TAG, "surface changed");
        while (!completed) {

        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                completed = false;
                if (mHolder.getSurface() == null) {
                    // preview surface does not exist
                    return;
                }

                try {
                    mCamera.stopPreview();
                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                }
                if (mCamera != null) {
                    mCamera.setDisplayOrientation(90);
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setRotation(90);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                    // parameters.set("jpeg-quality", 10);
                    parameters.setRecordingHint(true);
                    // parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    Log.v(TAG, "Recording size " + mPreviewSize.width + " " + mPreviewSize.height);
                    mCamera.setParameters(parameters);

                    // start preview with new settings
                    try {
                        mCamera.setPreviewDisplay(mHolder);
                        // mCamera.startPreview();


                    } catch (Exception e) {
                        Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                    }
                } else {
                    Log.v(TAG, "Camera error");
                }
            }
        });
        t.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!hideButNotKill) {
            destoryCamera();
        }
    }

    public void destoryCamera() {
        shutdown();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            mCamera.release();
            mCamera = null;
        }
    }

    private void shutdown() {
        // Release MediaRecorder and especially the Camera as it's a shared
        // object that can be used by other applications
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
        }
        // once the objects have been released they can't be reused
        mediaRecorder = null;
        mCamera = null;
    }

    //Camera changes

    public void showFrontFacing(Camera cam, Activity activity, int cameraId) {
        mCamera = cam;
        isFrontFacing = cameraId == 1;
        this.cameraId = cameraId;

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(90);

        mCamera.setDisplayOrientation(90);
        mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    public void turnOnFlash(boolean turnOn) {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(turnOn ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
    }

    public void toggleFlash() {
        flashIsOn = !flashIsOn;
    }

    public void toggleSelfie() {
        selfieIsOn = !selfieIsOn;
    }

    public void setSelfieIsOn(boolean selfieIsOn) {
        this.selfieIsOn = selfieIsOn;
    }


    //Video recorder
    public void initRecorder() {
        initRecorder(mHolder.getSurface());
    }

    public void initRecorder(Surface surface) {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        mediaRecorder.setOrientationHint(isFrontFacing ? 270 : 90);
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

        recordedFile = getOutputMediaFile(2);
        //Uri u = getOutputMediaFileUri(2);
        //recordedFile = new File(u.toString());


        // No limit. Don't forget to check the space on disk.
        // mediaRecorder.setMaxDuration(-1);
        // mediaRecorder.setVideoFrameRate(15);
        //mediaRecorder.setVideoSize(640, 480);
        mediaRecorder.setVideoSize(mPreviewSize.width, mPreviewSize.height);
        mediaRecorder.setOutputFile(recordedFile.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(mHolder.getSurface());

        //mediaRecorder.setVideoEncodingBitRate(1700000);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// MPEG_4_SP
        //mediaRecorder.setVideoFrameRate(16);
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startRecording() throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                initRecorder();
                mediaRecorder.start();
                videoRecordingStartedCallback.onRecording();
            }
        });
        t.start();
    }

    protected void stopRecording() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                //mCamera.release();
                mCamera.stopPreview();
                Log.v(TAG, "file size " + recordedFile.length());
                MediaModel mediaModel = obtainMediaModel(recordedFile, true);
                videoRecordedCallback.onRecorded(mediaModel);
            }
        });

        t.start();
    }

    //Take picture
    public void takePicture(View v) {
        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                turnOnFlash(false);
                final File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    MediaModel mediaModel = obtainMediaModel(pictureFile, false);
                    photoTakenCallback.onTaken(mediaModel);
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }
            }
        };

        if (flashIsOn && !selfieIsOn) {
            turnOnFlash(true);
        }
        mCamera.takePicture(null, null, pictureCallback);
    }

    //Helpers

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    /**
     * Create a File for saving an image or video
     */
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
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private MediaModel obtainMediaModel(File file, boolean isVideo) {
        MediaModel mediaModel = new MediaModel(file, selfieIsOn, isVideo);
        return mediaModel;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

}
