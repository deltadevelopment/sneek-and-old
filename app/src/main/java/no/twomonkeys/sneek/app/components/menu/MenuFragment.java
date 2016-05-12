package no.twomonkeys.sneek.app.components.menu;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;

/**
 * Created by simenlie on 10.05.16.
 */
public class MenuFragment extends android.support.v4.app.Fragment {

    private MoreButton viewsBtn;
    private MoreButton stalkersbtn;
    private MoreButton randomsBtn;
    private MoreButton stalkingBtn;
    private MoreButton fmlBtn;
    private boolean isShowing;
    public Callback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        LinearLayout rl = (LinearLayout) view.findViewById(R.id.centeredBox);

        viewsBtn = new MoreButton(getActivity());
        viewsBtn.updateTxt("68 VIEWS");
        viewsBtn.disableBtn();

        stalkersbtn = new MoreButton(getActivity());
        stalkersbtn.updateTxt("9 STALKERS");
        stalkersbtn.disableBtn();

        randomsBtn = new MoreButton(getActivity());
        randomsBtn.updateTxt("RANDOMS");

        stalkingBtn = new MoreButton(getActivity());
        stalkingBtn.updateTxt("STALKING");

        fmlBtn = new MoreButton(getActivity());
        fmlBtn.updateTxt("FML");


        randomsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFeed(0);
                callback.callbackCall(0);
            }
        });
        stalkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFeed(1);
                callback.callbackCall(1);
            }
        });
        fmlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callbackCall(2);
            }
        });


        swapColors();

        rl.addView(viewsBtn);
        rl.addView(stalkersbtn);
        rl.addView(randomsBtn);
        rl.addView(stalkingBtn);
        rl.addView(fmlBtn);
        ColorDrawable cd = new ColorDrawable(0xFF000000);
//255
        cd.setAlpha(0);
        view.setBackgroundDrawable(cd);


        return view;
    }

    public void dragRight(float x) {
        viewsBtn.moveRight(x);
        stalkersbtn.moveRight(x);
        randomsBtn.moveRight(x);
        stalkingBtn.moveRight(x);
        fmlBtn.moveRight(x);

        /*
        if (stalkersbtn.percentageScrolled() <= 1) {
            float percentage = (1 - stalkersbtn.percentageScrolled()) * 100;

            ColorDrawable cd = new ColorDrawable(0xFF000000);
            cd.setAlpha(calculate((int) percentage));
            getView().setBackgroundDrawable(cd);
        }
*/
    }

    public float percentageScrolled() {
        float percentage = (1 - stalkersbtn.percentageScrolled());
        return percentage;
    }

    public int percentageColor() {

        float percentage = (1 - stalkersbtn.percentageScrolled()) * 100;

        return calculate((int) percentage);

    }

    public boolean shouldChangeColor() {
        if (stalkersbtn.percentageScrolled() <= 1) {
            return true;
        }
        return false;
    }

    public void changeFeed(int row) {
        //Store the current here
        DataHelper.storeCurrentFeed(row);
        swapColors();
    }

    public void swapColors() {
        if (isSelected(0)) {
            randomsBtn.setTextColor(getResources().getColor(R.color.cyan));
            stalkingBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            randomsBtn.setTextColor(getResources().getColor(R.color.white));
            stalkingBtn.setTextColor(getResources().getColor(R.color.cyan));
        }
    }

    public boolean isSelected(int row) {
        int storedRow = DataHelper.currentFeed();
        if (storedRow == row) {
            return true;
        } else {
            return false;
        }
    }

    public void startMove(float x) {
        viewsBtn.startMove(x);
        stalkersbtn.startMove(x);
        randomsBtn.startMove(x);
        stalkingBtn.startMove(x);
        fmlBtn.startMove(x);
    }

    public void animateIn() {
        isShowing = true;
        viewsBtn.animateIn();
        stalkersbtn.animateIn();
        randomsBtn.animateIn();
        stalkingBtn.animateIn();
        fmlBtn.animateIn();
    }

    int calculate(int percentage) {
        int bigAlpha = 128;
        int result = (percentage * bigAlpha) / 100;
        return result;
    }

    public void animateOut() {
        isShowing = false;
        viewsBtn.animateOut();
        stalkersbtn.animateOut();
        randomsBtn.animateOut();
        stalkingBtn.animateOut();
        fmlBtn.animateOut();
    }

    public void show() {
        if (isShowing) {
            animateOut();
        } else {
            animateIn();
        }
    }

    public boolean isShowing() {
        return isShowing;
    }
}
