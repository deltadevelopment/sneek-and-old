package no.twomonkeys.sneek.app.components.feed;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;

/**
 * Created by simenlie on 10.05.16.
 */
public class TopBarFragment extends android.support.v4.app.Fragment {

    private final int startTopbarMargin = 10;
    public Button moreBtn;
    private Button cameraBtn;
    public SimpleCallback2 onMoreClb;
    public SimpleCallback2 onCameraClb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topbar_fragment, container, false);
        moreBtn = (Button) view.findViewById(R.id.moreBtn);
        cameraBtn = (Button) view.findViewById(R.id.cameraBtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMoreClb.callbackCall();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClb.callbackCall();
            }
        });

        return view;
    }

    public void onShowMenu(View v) {


    }

    public void onShowCam(View v) {
        Log.v("", "Clicked to cam");
    }

    public void drag(float yPos, float position) {
        //Log.v("TOPBAR","YPOS : "  + yPos + " : " + position);
        if (position != 0)
        {
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(getView().getLayoutParams());
            marginParams.setMargins(marginParams.leftMargin, startTopbarMargin - (int) position, 0, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            getView().setLayoutParams(layoutParams);
        }

    }

    public void updateAlpha(float alpha) {
        moreBtn.setAlpha(alpha);
        cameraBtn.setAlpha(alpha);
    }

    public void animateIn() {
        cameraBtn.animate().alpha(255).setDuration(150);
        moreBtn.animate().alpha(255).setDuration(150);
    }

    public void animateOut() {
        cameraBtn.animate().alpha(0).setDuration(150);
        moreBtn.animate().alpha(0).setDuration(150);
    }

}
