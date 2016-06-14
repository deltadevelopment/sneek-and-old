package no.twomonkeys.sneek.app.components.menu;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.DateHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;
import no.twomonkeys.sneek.app.shared.models.ViewsModel;

/**
 * Created by simenlie on 10.05.16.
 */
public class MenuFragment extends android.support.v4.app.Fragment {

    private MoreButton viewsBtn;
    private MoreButton stalkersbtn;
    private MoreButton stalkBtn;
    private MoreButton egotripBtn;
    private MoreButton fmlBtn;
    private boolean isShowing;
    public Callback callback;
    StoryModel storyModel;
    boolean hasStory;
    Date lastUpdatedStory, lastUpdated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        LinearLayout rl = (LinearLayout) view.findViewById(R.id.centeredBox);

        viewsBtn = new MoreButton(getActivity());
        viewsBtn.updateTxt("68 VIEWS", true);
        viewsBtn.disableBtn();

        stalkersbtn = new MoreButton(getActivity());
        stalkersbtn.updateTxt("9 STALKERS", true);
        stalkersbtn.disableBtn();

        stalkBtn = new MoreButton(getActivity());
        stalkBtn.updateTxt("STALK", true);
        stalkBtn.setTextColor(getResources().getColor(R.color.yellow));

        egotripBtn = new MoreButton(getActivity());
        egotripBtn.updateTxt("EGOTRIP", true);

        fmlBtn = new MoreButton(getActivity());
        fmlBtn.updateTxt("FML", true);


        stalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callbackCall(0);
            }
        });
        egotripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasStory){
                    callback.callbackCall(1);
                }
            }
        });
        fmlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callbackCall(2);
            }
        });


        //  rl.addView(viewsBtn);
        rl.addView(stalkersbtn);
        rl.addView(stalkBtn);
        rl.addView(egotripBtn);
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
        stalkBtn.moveRight(x);
        egotripBtn.moveRight(x);
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
        stalkBtn.startMove(x);
        egotripBtn.startMove(x);
        fmlBtn.startMove(x);
    }

    public void animateIn() {
        updateStalkers();
        isShowing = true;
        viewsBtn.animateIn();
        stalkersbtn.animateIn();
        stalkBtn.animateIn();
        egotripBtn.animateIn();
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
        stalkBtn.animateOut();
        egotripBtn.animateOut();
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


    public void updateStalkers() {
        if (lastUpdated == null) {
            lastUpdated = new Date();
            fetchData();
        } else {
            if (DateHelper.dateLaterThan(lastUpdated, 45)) {
                lastUpdated = new Date();
                fetchData();
            }
        }
        updateStory();
    }

    public void fetchData() {
        final UserModel userModel = new UserModel();
        userModel.setId(AuthHelper.getUserId());
        Log.v("AUTHHELPER","user id " + AuthHelper.getUserId());
        userModel.fetch(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                if (errorModel == null) {
                    String stalkerCellTxt = stalkersFormatted(userModel.getFollowers_count()) + " STALKERS";
                    stalkersbtn.updateTxt(stalkerCellTxt, false);
                    DataHelper.storeEmail(userModel.getEmail());
                }
            }
        });
    }


    public String stalkersFormatted(int stalkers) {
        float stalkersS = stalkers;
        float stalkersK = stalkersS / 1000;
        float stalkersM = stalkersK / 1000;
        float stalkersMr = stalkersM / 1000;
        String stalkersString;

        if (stalkersK > 1) {
            if (stalkersM > 1) {
                if (stalkersMr > 1) {
                    stalkersString = (int) stalkersMr + "mrd";
                } else {
                    stalkersString = (int) stalkersM + "m";
                }
            } else {
                stalkersString = (int) stalkersK + "k";

            }
        } else {
            stalkersString = (int) stalkersS + "";
        }
        return stalkersString;
    }


    public void updateStory() {
        if (DataHelper.shouldForceUpdateStory()) {
            lastUpdatedStory = null;
            DataHelper.setForceUpdateStory(false);
        }
        if (lastUpdatedStory == null) {
            lastUpdatedStory = new Date();
            fetchStory();
        } else {
            if (DateHelper.dateLaterThan(lastUpdatedStory, 45)) {
                lastUpdatedStory = new Date();
                fetchStory();
            } else {
                showOwnStreamButton();
            }
        }
    }

    public void fetchStory() {
        storyModel = new StoryModel(AuthHelper.getUserId());
        storyModel.fetch(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                if (errorModel == null) {
                    MomentModel momentModel = storyModel.getCurrentMoment();
                    if (momentModel != null) {
                        hasStory = true;
                    } else {
                        hasStory = false;
                    }
                    showOwnStreamButton();
                }
            }
        });
    }

    public void showOwnStreamButton() {
        if (hasStory && isVisible()) {
            //enable
            egotripBtn.enableBtn();
        } else {
            egotripBtn.disableBtn2();
            //disable
        }
    }
}
