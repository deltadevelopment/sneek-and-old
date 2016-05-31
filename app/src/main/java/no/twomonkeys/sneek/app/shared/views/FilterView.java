package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sherazkhilji.videffect.BlackAndWhiteEffect;
import com.sherazkhilji.videffect.CrossProcessEffect;
import com.sherazkhilji.videffect.DocumentaryEffect;
import com.sherazkhilji.videffect.DuotoneEffect;
import com.sherazkhilji.videffect.LamoishEffect;
import com.sherazkhilji.videffect.PosterizeEffect;
import com.sherazkhilji.videffect.TemperatureEffect;
import com.sherazkhilji.videffect.TintEffect;
import com.sherazkhilji.videffect.VignetteEffect;
import com.sherazkhilji.videffect.view.VideoSurfaceView;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.File;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDifferenceBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNormalBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.Camera.CaptionEditView;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;

/**
 * Created by simenlie on 30.05.16.
 */
public class FilterView extends RelativeLayout {

    private VideoSurfaceView mVideoView = null;
    private GLSurfaceView imageSurfaceView;
    private GPUImage mGPUImage;
    private MediaPlayer mMediaPlayer = null;
    private String filePath;
    private Context c;
    private int currentPosition;
    private boolean isVideo;


    public FilterView(Context context) {
        super(context);
        initializeViews(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //setVideo("/storage/emulated/0/Pictures/MyCameraApp/VID_20160530_143922.mp4");


    }

    public void setVideo(String filePath, Context c, boolean isSelfie) {
        isVideo = true;
        this.filePath = filePath;
        this.c = c;
        setVideo(0, false, isSelfie);
    }

    public void setImage(Bitmap bitmap) {
        isVideo = false;
        RelativeLayout l = (RelativeLayout) findViewById(R.id.filterView);
        imageSurfaceView = new GLSurfaceView(getContext());
        imageSurfaceView.setZOrderMediaOverlay(true);

        LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageSurfaceView.setLayoutParams(rlp);
        l.addView(imageSurfaceView);

        mGPUImage = new GPUImage(getContext());
        mGPUImage.setGLSurfaceView(imageSurfaceView);
        mGPUImage.setImage(bitmap); // this loads image on the current thread, should be run in a thread

        //mGPUImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null);
    }

    public void applyNextFilter(int filterNumber) {
        if (isVideo) {
            applyFilterToVideo(filterNumber);
        } else {
            applyFilterToImage(filterNumber);
        }
    }

    public void applyFilterToImage(int filterNumber) {
        switch (filterNumber) {
            case 0: {
                mGPUImage.setFilter(new GPUImageFilter());
                break;
            }
            case 1: {
                mGPUImage.setFilter(new GPUImageGrayscaleFilter());
                break;
            }
            case 2: {
                mGPUImage.setFilter(new VintageFilter(getContext()));
                break;
            }
        }

        //GPUImageContrastFilter
    }

    public void applyFilterToVideo(int filterNumber) {
        mMediaPlayer.pause();
        currentPosition = mMediaPlayer.getCurrentPosition();
        simpleRemove();

        RelativeLayout l = (RelativeLayout) findViewById(R.id.filterView);
        l.addView(mVideoView);

        if (filterNumber == 0) {
            mVideoView.init(mMediaPlayer,
                    null, true);
        } else if (filterNumber == 1) {
            mVideoView.init(mMediaPlayer,
                    new BlackAndWhiteEffect(), true);
        } else if (filterNumber == 2) {
            mVideoView.init(mMediaPlayer,
                    new CrossProcessEffect(), true);
        }
    }

    public void setVideo(int filterNumber, boolean isUsed, boolean isSelfie) {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(currentPosition);
                mp.setLooping(true);
                mp.start();
            }
        });
        try {
            mMediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideoView = new VideoSurfaceView(c, mMediaPlayer, new VideoSurfaceView.FilterAppliedCallback() {
            @Override
            public void onFiltered() {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            }
        }, isSelfie);

        mVideoView.setZOrderMediaOverlay(true);
        mVideoView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(params);

        RelativeLayout l = (RelativeLayout) findViewById(R.id.filterView);
        l.addView(mVideoView);
        mVideoView.init(mMediaPlayer,
                null, isUsed);
    }

    public void stop() {
        mMediaPlayer.stop();
    }


    public void simpleRemove() {
        // mVideoView.invalidate();
        RelativeLayout l = (RelativeLayout) findViewById(R.id.filterView);
        l.removeView(mVideoView);
    }

    public void remove() {
        RelativeLayout l = (RelativeLayout) findViewById(R.id.filterView);
        if (isVideo) {
            mVideoView.invalidate();
            mVideoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            l.removeView(mVideoView);
        } else {
            l.removeView(imageSurfaceView);
        }
    }
}
