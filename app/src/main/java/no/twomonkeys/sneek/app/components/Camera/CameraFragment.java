package no.twomonkeys.sneek.app.components.Camera;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
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

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.menu.MoreButton;

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
            Log.v("CAMERANULL", " nUL CAMRA");
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this.getActivity(), mCamera);
        preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        //preview.addView(mPreview);
        //mCamera.stopPreview();

        recordBtn = (ImageButton) view.findViewById(R.id.button_capture);
        recordBtn.setImageResource(R.drawable.circle);
        recordBtn.setColorFilter(Color.argb(255, 255, 255, 255));


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        defaultWidth = width;
        rl.setX(defaultWidth);

        return view;
    }

    public void drag(float x) {
        float result = x + dX;


        getView().setX(result);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        preview.removeView(mPreview);
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            mCamera.release();
            mCamera = null;
        }
    }

    public void startMove(float x) {
        dX = getView().getX() - x;
    }

    public void animateIn() {
        getView().animate().translationX(0).setDuration(150);
        //Do something with cam here ??
    }

    public void animateOut() {
        getView().animate().translationX(defaultWidth).setDuration(150);
    }

    public void prepareCamera() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCamera != null){
                    mCamera.startPreview();
                }

            }
        });
        t.start();
    }

    public void stopCamera() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCamera != null){
                    mCamera.stopPreview();
                }

            }
        });
        t.start();
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
