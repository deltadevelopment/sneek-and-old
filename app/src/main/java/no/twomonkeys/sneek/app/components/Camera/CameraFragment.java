package no.twomonkeys.sneek.app.components.Camera;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.views.BoolCallback;
import no.twomonkeys.sneek.app.shared.views.SneekVideoView;
import retrofit2.http.Url;

/**
 * Created by simenlie on 11.05.16.
 */
public class CameraFragment extends Fragment {
    private Camera mCamera;
    private CameraPreview mPreview;
    // private FrameLayout preview;
    private ImageButton recordBtn;
    private boolean cameraFront;
    private int cameraId;
    private float dX;
    private int defaultWidth;
    FrameLayout preview;
    boolean previewIsRunning, flashIsOn, selfieIsOn;
    CameraEditView cameraEditView;
    Button backBtn, selfieBtn, flashBtn;
    public SimpleCallback onCancelClb;
    public BoolCallback onLockClb;
    ProgressBar progressBar;
    boolean isLongPressing;
    Timer timer;
    float updateValue;
    float percent;
    float percentHundred;
    boolean isRecording;

    public interface VideoDoneCallback {
        void onRecorded(File file);
    }

    public VideoDoneCallback videoDone;

    private interface ImageProcessedCallback {
        void onProccesed(File file);
    }

    ImageProcessedCallback imageProcessedCallback;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.camera_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.cameraFragment);
        //cameraManager = new CameraManager();


        // Create an instance of Camera
        mCamera = getCameraInstance(0);
        if (mCamera == null) {
            Log.v("CAMERANULL", " NULL CAMRA");
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this.getActivity(), mCamera, getActivity());


        mPreview.videoRecordedCallback = new CameraPreview.VideoRecordedCallback() {
            @Override
            public void onRecorded(final File file) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        cameraEditView.setVisibility(View.VISIBLE);
                        cameraEditView.addMovie(file, getContext());
                        //videoDone.onRecorded(file);
                        onLockClb.callbackCall(true);
                        hideButtons();

                        //loadVideo(file);

                    }//public void run() {
                });



            }
        };
        preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        //mCamera.stopPreview();

        recordBtn = (ImageButton) view.findViewById(R.id.button_capture);
        recordBtn.setImageResource(R.drawable.circle);
        recordBtn.setColorFilter(Color.argb(255, 255, 255, 255));
        recordBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("LONG PRESS", "LONG PRESSING");
                onLockClb.callbackCall(true);
                isLongPressing = true;
                startRecording();
                return false;
            }
        });
        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!isLongPressing) {
                        takePicture(v);
                    } else {
                        stopRecording();
                    }
                    isLongPressing = false;
                    onLockClb.callbackCall(false);
                }
                return false;
            }
        });


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        defaultWidth = width;
        rl.setX(defaultWidth);

        cameraEditView = (CameraEditView) view.findViewById(R.id.cameraEditView);
        cameraEditView.setVisibility(View.INVISIBLE);
        cameraEditView.onCancelEdit = new SimpleCallback() {
            @Override
            public void callbackCall() {
                showButtons();
                mCamera.startPreview();
                onLockClb.callbackCall(false);
            }
        };
        cameraEditView.onMediaPosted = new SimpleCallback() {
            @Override
            public void callbackCall() {
                onLockClb.callbackCall(false);
                onCancelClb.callbackCall();
                showButtons();
                mCamera.stopPreview();
            }
        };

        backBtn = (Button) view.findViewById(R.id.cameraBackBtn);
        flashBtn = (Button) view.findViewById(R.id.cameraFlashBtn);
        selfieBtn = (Button) view.findViewById(R.id.cameraSelfieBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClb.callbackCall();
            }
        });
        flashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });
        selfieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelfie();
            }
        });

        //applyUiToButton(backBtn, "BACK");

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        float time = 15;
        updateValue = time / (time * time);

        progressBar.setProgress(0);



        return view;
    }




    public void incrementSpin() {
        if (percentHundred <= 100) {
            percent += updateValue;
            percentHundred = percent;
            float maxValue = 500;
            float percentageOfMax = (percentHundred * maxValue) / 100;
            progressBar.setProgress((int) percentageOfMax);
            //ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
            //animation.setDuration(5000); //in milliseconds
            //animation.setInterpolator(new DecelerateInterpolator());
            //animation.start();
            //Log.v("Incrementing", "increment " + percentageOfMax + " " + percentHundred);
        } else {
            stopRecording();
        }
    }

    public void startRecording() {
        if (!isRecording) {
            isRecording = true;
            timer = new Timer();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPreview.startRecording();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Your code to run in GUI thread here
                                        incrementSpin();
                                    }//public void run() {
                                });






                            }
                        }, 10, 10);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();



            Log.v("START RECORDING HERE", "START RECORDING");
        }
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            timer.cancel();
            percentHundred = 0;
            percent = 0;
            progressBar.setProgress(0);
            mPreview.stopRecording();
            Log.v("STOP RECORDING HERE", "STOP RECORDING");
        }
    }


    public void toggleFlash() {
        if (flashIsOn) {
            flashBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            flashBtn.setTextColor(getResources().getColor(R.color.cyan));
        }
        flashIsOn = !flashIsOn;
    }

    public void toggleSelfie() {
        if (selfieIsOn) {
            selfieBtn.setTextColor(getResources().getColor(R.color.white));
            flashBtn.setVisibility(View.VISIBLE);
        } else {
            selfieBtn.setTextColor(getResources().getColor(R.color.cyan));
            flashBtn.setVisibility(View.INVISIBLE);
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mCamera.release();
                int cameraId = selfieIsOn ? 0 : 1;
                mCamera = getCameraInstance(cameraId);
                mPreview.showFrontFacing(mCamera, getActivity(), cameraId);
                selfieIsOn = !selfieIsOn;
            }
        });
        t.start();
    }

    public void cancelCamera() {

    }

    public void applyUiToButton(Button b, String txt) {
        b.setBackgroundColor(getResources().getColor(R.color.black));
        b.setTextColor(getResources().getColor(R.color.white));
        b.setTextSize(17);

        b.setTypeface(Typeface.create("HelveticaNeue", 0));

        b.setText(txt);
        int margin = UIHelper.dpToPx(getContext(), 10);
        int btnHeight = UIHelper.dpToPx(getContext(), 50);

        b.setPadding(margin, 0, margin, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.height = btnHeight;
        params.setMargins(0, margin, 0, 0);

        //rl.addView(button);
        Log.v("WIDTH: ", "" + params.width);
        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(b.getTypeface());// your preference here
        paint.setTextSize(b.getTextSize());// have this the same as your text size

        String text = txt;

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height = bounds.height();
        text_width = bounds.width() + (margin * 2) + 10;
        params.width = text_width;
        b.setLayoutParams(params);
        //width = text_width;
        //b.setX(-width);
    }

    public void drag(float x) {
        float result = x + dX;
        Log.v("result is", "result " + result);
        if (result >= 0) {
            getView().setX(result);
        }
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


                    processImage(pictureFile, new ImageProcessedCallback() {
                        @Override
                        public void onProccesed(File file) {
                            cameraEditView.addPhoto(file);
                            cameraEditView.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }

                onLockClb.callbackCall(true);
                hideButtons();
            }
        };

        if (flashIsOn && !selfieIsOn) {
            turnOnFlash(true);
        }

        mCamera.takePicture(null, null, pictureCallback);
    }

    public void turnOnFlash(boolean turnOn) {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(turnOn ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
    }

    public void turnOnTorch(boolean turnOn) {

    }

    public void processImage(final File file, final ImageProcessedCallback ipc) {
        //Should check here that it is a nexus phone also
        if (selfieIsOn) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    matrix.preScale(-1, 1);

                    Bitmap originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth(), originalBitmap.getHeight(), true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                    //create a file to write bitmap data
                    final File f = new File(getActivity().getCacheDir(), "tmp.jpeg");
                    try {
                        f.createNewFile();
                        //Convert bitmap to byte array
                        Bitmap bitmap = rotatedBitmap;
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Your code to run in GUI thread here
                                ipc.onProccesed(f);
                            }//public void run() {
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        } else {
            ipc.onProccesed(file);
        }
    }

    public void hideButtons() {
        backBtn.setVisibility(View.INVISIBLE);
        selfieBtn.setVisibility(View.INVISIBLE);
        flashBtn.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        backBtn.setVisibility(View.VISIBLE);
        selfieBtn.setVisibility(View.VISIBLE);
        if (!selfieIsOn) {
            flashBtn.setVisibility(View.VISIBLE);
        }
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

    public void startMove(float x) {
        dX = getView().getX() - x;
    }

    public void animateIn() {
        getView().animate().translationX(0).setDuration(150);
        //Do something with cam here ??
        prepareCamera();
    }

    public void animateOut() {
        getView().animate().translationX(defaultWidth).setDuration(150);
    }

    public void prepareCamera() {
        if (!previewIsRunning && mCamera != null) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    previewIsRunning = true;
                    mCamera.startPreview();
                    //mPreview.initRecorder(mPreview.mHolder.getSurface());


                }
            });
            t.start();

        }
    }

    public void stopCamera() {
        if (previewIsRunning && mCamera != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    previewIsRunning = false;
                    mCamera.stopPreview();
                }
            });
            t.start();
        }
    }


    public void test(View view) {
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        marginParams.setMargins(40, 0, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        view.setLayoutParams(layoutParams);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int id) {
        Camera c = null;
        try {
            c = Camera.open(id); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.v("CAMAR", "ERROR");
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private static int findFrontFacingCamera() {

        // Search for the front facing camera
        int cameraId = 0;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                return cameraId;
            }
        }
        return cameraId;
    }


}
