package no.twomonkeys.sneek.app.components.Camera;

import android.animation.Animator;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.MediaModel;
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
    boolean previewIsRunning;//, flashIsOn, selfieIsOn;
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
    ProgressBar focusCircle;

    public interface VideoDoneCallback {
        void onRecorded(File file);
    }

    public interface AnimatedCallback {
        void onAnimated();
    }

    public AnimatedCallback animatedCallback;
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


        preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        // Create our Preview view and set it as the content of our activity.
        initCamera();
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
                        //takePicture(v);
                        mPreview.takePicture(v);
                        hideButtons();
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
                //cameraEditView.videoView.setVisibility(View.INVISIBLE);
                //cameraEditView.videoView.setZOrderMediaOverlay(false);
                //cameraEditView.videoView.setZOrderOnTop(false);
                // cameraEditView.videoView.setVisibility(View.INVISIBLE);
                cameraEditView.setVisibility(View.INVISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //mCamera.startPreview();
                //initCamera();
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
                //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        mPreview.photoTakenCallback = new CameraPreview.PhotoTakenCallback() {
            @Override
            public void onTaken(MediaModel mediaModel) {
                final MediaModel m = mediaModel;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLockClb.callbackCall(true);
                        cameraEditView.addMedia(m);
                        cameraEditView.setVisibility(View.VISIBLE);
                    }//public void run() {
                });

            }
        };

        mPreview.videoRecordingStartedCallback = new CameraPreview.VideoRecordingStartedCallback() {
            @Override
            public void onRecording() {
                startRecordingAnimation();
            }
        };

        focusCircle = (ProgressBar) view.findViewById(R.id.focusCircle);
        focusCircle.setAlpha(0);
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

    public void initCamera() {
        mCamera = getCameraInstance(0);
        if (mCamera == null) {
            Log.v("CAMERANULL", " NULL CAMRA");
        }
        mPreview = new CameraPreview(this.getActivity(), mCamera, getActivity());


        mPreview.videoRecordedCallback = new CameraPreview.VideoRecordedCallback() {
            @Override
            public void onRecorded(final MediaModel mediaModel) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        cameraEditView.setVisibility(View.VISIBLE);
                        //cameraEditView.addMovie(file, getContext());
                        cameraEditView.addMedia(mediaModel);
                        //videoDone.onRecorded(file);
                        onLockClb.callbackCall(true);
                        hideButtons();

                        //loadVideo(file);

                    }//public void run() {
                });
            }
        };
        mPreview.touchCallback = new CameraPreview.TouchCallback() {
            @Override
            public void onTouch(int x, int y) {
                animateFocus(x, y);
            }
        };

        preview.addView(mPreview);
    }

    public void animateFocus(int x, int y) {
        focusCircle.setX(x - (focusCircle.getWidth() / 2));
        focusCircle.setY(y - (focusCircle.getHeight() / 2));

        ObjectAnimator anim = ObjectAnimator.ofFloat(focusCircle, "alpha", 1);
        anim.setInterpolator(new LinearInterpolator());
        anim.setInterpolator(new AccelerateInterpolator());

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                focusCircle.animate().alpha(0).setDuration(250).setStartDelay(150);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.setDuration(150).start();
    }

    public void startRecording() {
        if (!isRecording) {
            isRecording = true;
            timer = new Timer();
            try {
                mPreview.startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("START RECORDING HERE", "START RECORDING");
        }
    }

    public void startRecordingAnimation() {
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
    }

    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            timer.cancel();
            percentHundred = 0;
            percent = 0;
            progressBar.setProgress(0);
            // mPreview.stopRecording();
            mPreview.stopRecording();
            Log.v("STOP RECORDING HERE", "STOP RECORDING");
        }
    }


    public void toggleFlash() {
        if (mPreview.flashIsOn) {
            flashBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            flashBtn.setTextColor(getResources().getColor(R.color.cyan));
        }
        mPreview.toggleFlash();
    }

    public void toggleSelfie() {
        if (mPreview.selfieIsOn) {
            selfieBtn.setTextColor(getResources().getColor(R.color.white));
            flashBtn.setVisibility(View.VISIBLE);
        } else {
            selfieBtn.setTextColor(getResources().getColor(R.color.cyan));
            flashBtn.setVisibility(View.INVISIBLE);
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCamera != null) {
                    mCamera.release();
                    //mPreview.releaseRecorder();
                    int cameraId = mPreview.selfieIsOn ? 0 : 1;
                    mCamera = getCameraInstance(cameraId);
                    mPreview.showFrontFacing(mCamera, getActivity(), cameraId);
                    mPreview.toggleSelfie();
                } else {
                    //Camera is null
                }

            }
        });
        t.start();
    }

    public void releaseCamera() {
        mPreview.destoryCamera();

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

    public void turnOnTorch(boolean turnOn) {

    }

    public void hideButtons() {
        backBtn.setVisibility(View.INVISIBLE);
        selfieBtn.setVisibility(View.INVISIBLE);
        flashBtn.setVisibility(View.INVISIBLE);
        recordBtn.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        backBtn.setVisibility(View.VISIBLE);
        selfieBtn.setVisibility(View.VISIBLE);
        recordBtn.setVisibility(View.VISIBLE);
        if (!mPreview.selfieIsOn) {
            flashBtn.setVisibility(View.VISIBLE);
        }
    }


    public void startMove(float x) {
        dX = getView().getX() - x;
    }

    public void animateIn() {
        // cameraEditView.setVisibility(View.INVISIBLE);
        //getView().animate().translationX(0).setDuration(150);
        //Do something with cam here ??


        ObjectAnimator anim = ObjectAnimator.ofFloat(getView(), "translationX", 0);
        anim.setInterpolator(new LinearInterpolator());
        anim.setInterpolator(new AccelerateInterpolator());

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPreview.hide(false);
                prepareCamera();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.setDuration(150).start();

    }

    public void animateOut() {
        animatedCallback.onAnimated();
        //getView().animate().translationX(defaultWidth).setDuration(150);
        ObjectAnimator anim = ObjectAnimator.ofFloat(getView(), "translationX", defaultWidth);
        anim.setInterpolator(new LinearInterpolator());
        anim.setInterpolator(new AccelerateInterpolator());

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //mPreview.setVisibility(View.INVISIBLE);
                mPreview.hide(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.setDuration(150).start();
    }

    public void prepareCamera() {
        if (!previewIsRunning && mCamera != null) {
            // mPreview.setVisibility(View.VISIBLE);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    previewIsRunning = true;
                    mCamera.startPreview();
                    //mPreview.initRecorder();
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
