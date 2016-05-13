package no.twomonkeys.sneek.app.components.story;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.menu.MoreButton;

/**
 * Created by simenlie on 13.05.16.
 */
public class StoryFragment extends android.support.v4.app.Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.story_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.storyFragment);
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        view.setY(size.y);

        return view;
    }

    public void animateIn() {
        view.animate().translationY(0).setDuration(250);
    }

}
