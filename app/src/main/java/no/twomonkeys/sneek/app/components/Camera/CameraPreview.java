package no.twomonkeys.sneek.app.components.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
    MediaRecorder mrec;
    MediaRecorder mediaRecorder;
    File recordedFile;
    Activity parentActivity;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    boolean isFrontFacing;
    VideoRenderer2 vr2;
    boolean isRecording;
    int imageFormat;

    //Callbacks
    public interface PhotoTakenCallback {
        void onTaken(MediaModel mediaModel);
    }

    public PhotoTakenCallback photoTakenCallback;

    public interface VideoRecordedCallback {
        void onRecorded(File file);
    }

    public VideoRecordedCallback videoRecordedCallback;

    public CameraPreview(Context context, Camera camera, Activity activity) {
        super(context);
        mCamera = camera;

        vr2 = new VideoRenderer2();
        /*
        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                try {
                    //Log.v("FRAME", "FRAME IS HERE");
                    Camera.Parameters parameters = camera.getParameters();
                    imageFormat = parameters.getPreviewFormat();
                    if (imageFormat == ImageFormat.NV21) {
                        Rect rect = new Rect(0, 0, mPreviewSize.width, mPreviewSize.height);
                        YuvImage img = new YuvImage(data, ImageFormat.NV21, mPreviewSize.width, mPreviewSize.height, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compressToJpeg(rect, 100, baos);
                        byte[] imageData = baos.toByteArray();
                        Bitmap previewBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        if (isRecording) {
                            vr2.recieveFrame(previewBitmap);
                        }
                    }


                } catch (Exception e) {

                }
            }

        };
*/
        // mCamera.setPreviewCallback(previewCallback);
        this.parentActivity = activity;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();

        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void showFrontFacing(Camera cam, Activity activity, int cameraId) {
        mCamera = cam;

        isFrontFacing = cameraId == 1;

        this.cameraId = cameraId;
        mCamera.setDisplayOrientation(getRotation2(activity, cameraId));

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
        //initRecorder();
    }

    public int getRotation2(Activity activity, int cameraId) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        destoryCamera();
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

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(90);
        parameters.setRecordingHint(true);
        parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            // mCamera.startPreview();


        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        Log.v("on measure", "on measure");

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }


    }

    public void initRecorder() {
        initRecorder(mHolder.getSurface());
    }


    //Record video parameters

    protected void startRecording() throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                initRecorder();
                mediaRecorder.start();
            }
        });
        t.start();
    }

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

    public void turnOnFlash(boolean turnOn) {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(turnOn ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
    }


    public void initRecorder(Surface surface) {

        Log.v("INIT RECORDER", "INITING RECORDER");

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }


        mediaRecorder.setOrientationHint(isFrontFacing ? 270 : 90);
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH));
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

        recordedFile = getOutputMediaFile(2);
        Log.v("File path", "path + " + recordedFile.getAbsolutePath());
        //Uri u = getOutputMediaFileUri(2);
        //recordedFile = new File(u.toString());

        mediaRecorder.setOutputFile(recordedFile.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(getHolder().getSurface());

        // No limit. Don't forget to check the space on disk.
        // mediaRecorder.setMaxDuration(-1);
        // mediaRecorder.setVideoFrameRate(15);
        mediaRecorder.setVideoSize(640, 480);

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

    protected void stopRecording() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                //mCamera.release();
                mCamera.stopPreview();
                Log.v("File size", "file size " + recordedFile.length());
                videoRecordedCallback.onRecorded(recordedFile);
            }
        });

        t.start();


    }


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

}
