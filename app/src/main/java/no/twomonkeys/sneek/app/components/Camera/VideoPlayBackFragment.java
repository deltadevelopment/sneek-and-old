package no.twomonkeys.sneek.app.components.Camera;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.views.CaptionView;
import no.twomonkeys.sneek.app.shared.views.LoadingView;
import no.twomonkeys.sneek.app.shared.views.MoreView;
import no.twomonkeys.sneek.app.shared.views.SneekVideoView;

/**
 * Created by simenlie on 24.05.16.
 */
public class VideoPlayBackFragment extends Fragment {

    SneekVideoView videoView;

    private int mPlayerPosition;
    private MediaController mediaControls;
    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        Log.v("CREATED", "on created");
        View rootView = inflater.inflate(
                R.layout.video_playback_fragment, container, false);
        Bundle args = getArguments();


        videoView = (SneekVideoView) rootView.findViewById(R.id.videoSneekVideoView);


        return rootView;
    }

    public void loadVideo(File f) {

        if (mediaControls == null) {
            mediaControls = new MediaController(getContext());

        }
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.v("Got here", "hello there");
                mPlayerPosition = videoView.getCurrentPosition();
                //videoView.setVideoPath(temp.getAbsolutePath());
                // videoView.setVideoURI(uri);
                videoView.resume();
                videoView.requestFocus();
                //mediaPlayer.seekTo(mPlayerPosition);
                return true;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.v("Prepared", "prepared");
                mediaPlayer = mp;
                //videoHelper.setMediaPlayer(mediaPlayer);
                mp.setLooping(true);

                videoView.start();

                //videoView.setVisibility(View.VISIBLE);

            }
        });

        videoView.setVideoURI(Uri.fromFile(f));
        videoView.requestFocus();
        videoView.start();

    }




}
