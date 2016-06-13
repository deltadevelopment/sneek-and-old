package no.twomonkeys.sneek.app.components.story;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.DateHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.helpers.VideoHelper;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.views.CaptionView;
import no.twomonkeys.sneek.app.shared.views.LoadingFragment;
import no.twomonkeys.sneek.app.shared.views.LoadingView;
import no.twomonkeys.sneek.app.shared.views.MoreView;
import no.twomonkeys.sneek.app.shared.views.ProgressIndicator;
import no.twomonkeys.sneek.app.shared.views.SneekVideoView;

/**
 * Created by simenlie on 13.05.16.
 */

// Instances of this class are fragments representing a single
// object in our collection.
public class MomentFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private static final String TAG = "MomentFragment";
    private static SimpleDraweeView draweeView;
    private MomentModel momentModel;
    private LoadingView loadingView;
    private SneekVideoView videoView;
    ControllerListener controllerListener;

    private boolean isVisible;
    private MediaController mediaControls;
    private MediaPlayer mediaPlayer;
    private int mPlayerPosition;
    private Handler mHandler;
    private File temp;
    private int totalRead;
    private boolean started;

    VideoHelper videoHelper;
    TextView momentCaption, usernameTxt;
    CaptionView captionView;
    boolean hasLayedOut;
    TextView updatedTxt;


    public MomentFragment() {

    }

    public void setMomentModel(MomentModel momentModel) {
        this.momentModel = momentModel;
        Log.v("MOMENT MODEL", " id " + momentModel.id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        Log.v("CREATED", "on created");
        View rootView = inflater.inflate(
                R.layout.moment_fragment, container, false);
        Bundle args = getArguments();


        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.momentFragment);
        draweeView = (SimpleDraweeView) rootView.findViewById(R.id.draweeView);
        loadingView = (LoadingView) rootView.findViewById(R.id.storyLoadingView);

        videoView = (SneekVideoView) rootView.findViewById(R.id.momentVideoView);
        videoView.setVisibility(View.INVISIBLE);

        GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
        hierarchy.setFadeDuration(0);
        hierarchy.setPlaceholderImage(R.drawable.splash2);

        Bundle bundle = getArguments();
        loadingView.startAnimate();

        //here is your list array
        String[] myStrings = bundle.getStringArray("elist");
        Log.v("arr", "args" + getArguments());


        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(200, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //params2.width = width;
        params2.setMargins(0,UIHelper.dpToPx(getContext(), 30),0,0);
        params2.height = UIHelper.dpToPx(getContext(), 30);
        momentCaption = (TextView) rootView.findViewById(R.id.momentCaption);
        momentCaption.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        momentCaption.setLayoutParams(params2);
        momentCaption.setTextColor(getContext().getResources().getColor(R.color.black));

        momentCaption.setVisibility(View.INVISIBLE);

        captionView = (CaptionView) rootView.findViewById(R.id.captionView);

        updatedTxt = (TextView) rootView.findViewById(R.id.updatedTxt);
        usernameTxt = (TextView) rootView.findViewById(R.id.usernameTxt);


        updateView();


        return rootView;
    }


    public void updateView() {
        if (momentModel != null && draweeView != null) {

            if (momentModel.media_type == 0) {
                momentModel.loadPhoto(draweeView, new SimpleCallback2() {
                    @Override
                    public void callbackCall() {
                        loadingView.stopAnimation();
                    }
                });
            } else {
                loadVideo();
            }
            updateCaption();
            usernameTxt.setText(momentModel.getUserModel().getUsername().toUpperCase());
            updatedTxt.setText(DateHelper.shortTimeSince(momentModel.getCreated_at()));
        }
    }

    public void updateCaption() {
        if (momentModel.getCaption() != "") {
            final String caption = momentModel.getCaption();
            momentCaption.setText(caption);
            momentCaption.setTextSize(15);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            Rect bounds = UIHelper.sizeForView(momentCaption, caption);
            int margin = UIHelper.dpToPx(getContext(), 10);
            params.height = UIHelper.dpToPx(getContext(), 30);
            params.width = bounds.width() + margin + 10;
            //left, top, right, bottom

            momentCaption.setPadding(margin / 2, margin / 2, margin / 2, 0);
            params.setMargins(margin, margin, margin + UIHelper.dpToPx(getContext(), 50), 0);

            momentCaption.setLayoutParams(params);
            //momentCaption.getLayout().getLineStart(1);

            ViewTreeObserver vto = momentCaption.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!hasLayedOut) {
                        hasLayedOut = true;
                        Layout layout = momentCaption.getLayout();
                        Log.v("Line count is", "line count: " + layout.getLineStart(1));
                        //layoutCaption(layout.getLineCount(), layout.getLineStart(layout.getLineCount() - 1));
                        captionView.updateCaption(layout.getLineCount(), layout.getLineStart(layout.getLineCount() - 1), caption);
                    }
                }
            });
        } else {
            Log.v("CAPTION0", "IS NIL");
            captionView.setVisibility(View.INVISIBLE);
        }
    }

    public void layoutCaption(int numberOfLines, int secondLineStart) {
        if (numberOfLines > 1) {
            String lineOne = momentCaption.getText().toString().substring(0, secondLineStart);
            String lineTwo = momentCaption.getText().toString().substring(secondLineStart, momentCaption.getText().toString().length() - 1);
            Log.v("PRInt", "lineone :" + lineOne + " line tow : " + lineTwo);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                isVisible = false;
                //pause or stop video
                if (videoView != null) {
                    //videoView.stopPlayback();
                    videoView.pause();

                }
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                //play your video
                isVisible = true;
                Log.v("STARING AGAIN", " DId start again");
                //videoView.start();

                videoView.resume();
            }
        }
    }

    public void loadVideo() {
        Log.v("Got here", "hello there " + momentModel.getMedia_url());

        //set the media controller buttons
        videoView.setVisibility(View.VISIBLE);
        if (mediaControls == null) {
            mediaControls = new MediaController(getContext());
        }
        // videoView.setMediaController(mediaControls);
        Uri uri = Uri.parse(momentModel.getMedia_url());

        //videoView.setVideoURI(uri);
        //videoView.requestFocus();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.v("Got here", "hello there");
                mPlayerPosition = videoView.getCurrentPosition();
                //videoView.setVideoPath(temp.getAbsolutePath());
                // videoView.setVideoURI(uri);
                if (isVisible){
                    videoView.resume();
                }

                videoView.requestFocus();
                //mediaPlayer.seekTo(mPlayerPosition);
                return true;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.v(TAG, "prepared");
                mediaPlayer = mp;
                videoHelper.setMediaPlayer(mediaPlayer);
                mp.setLooping(true);
                loadingView.stopAnimation();
                if (isVisible) {
                    Log.v(TAG, "VISIBLE NO??");
                    videoView.start();
                }
            }
        });
        final MomentFragment self = this;

        videoHelper = new VideoHelper(videoView, momentModel, getActivity());
        videoHelper.loadVideo();
    }

    public void stopVideo() {
        if (videoView != null) {
            videoView.pause();

        }
    }

    public MomentModel getMomentModel() {
        return momentModel;
    }
}
