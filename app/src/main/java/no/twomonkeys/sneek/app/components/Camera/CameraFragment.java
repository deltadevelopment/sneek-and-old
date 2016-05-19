package no.twomonkeys.sneek.app.components.Camera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.menu.MoreButton;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.views.BoolCallback;

/**
 * Created by simenlie on 11.05.16.
 */
public class CameraFragment extends android.support.v4.app.Fragment {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.cameraFragment);
        //cameraManager = new CameraManager();


        // Create an instance of Camera
        mCamera = getCameraInstance();
        if (mCamera == null) {
            Log.v("CAMERANULL", " NULL CAMRA");
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this.getActivity(), mCamera);
        preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        //mCamera.stopPreview();

        recordBtn = (ImageButton) view.findViewById(R.id.button_capture);
        recordBtn.setImageResource(R.drawable.circle);
        recordBtn.setColorFilter(Color.argb(255, 255, 255, 255));
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(v);
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

        return view;
    }


    public void toggleFlash() {
        if (flashIsOn) {
//Turn flash off
            flashBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
//turn flash on
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
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);

        mCamera.release();
        mCamera = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mPreview.showFrontFacing(selfieIsOn ? 0 : 1);
            }
        });
        t.start();

        selfieIsOn = !selfieIsOn;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        preview.removeView(mPreview);
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            mCamera.release();
            mCamera = null;
        }
        */
    }

    public void takePicture(View v) {

        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    cameraEditView.addPhoto(pictureFile);
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }
                cameraEditView.setVisibility(View.VISIBLE);
                onLockClb.callbackCall(true);
                hideButtons();
            }
        };

        mCamera.takePicture(null, null, pictureCallback);

    }

    public void hideButtons() {
        backBtn.setVisibility(View.INVISIBLE);
        selfieBtn.setVisibility(View.INVISIBLE);
        flashBtn.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        backBtn.setVisibility(View.VISIBLE);
        selfieBtn.setVisibility(View.VISIBLE);
        flashBtn.setVisibility(View.VISIBLE);
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
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
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
