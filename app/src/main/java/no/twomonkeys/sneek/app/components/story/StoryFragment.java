package no.twomonkeys.sneek.app.components.story;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.MainActivity;
import no.twomonkeys.sneek.app.components.menu.MoreButton;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DirectionHelper;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 13.05.16.
 */
public class StoryFragment extends android.support.v4.app.Fragment {

    View view;
    DirectionHelper direction;
    float touchActionDownX, touchActionDownY, touchActionMoveX, touchActionMoveY, lastX;
    boolean touchActionMoveStatus, lastScrolledUp, isClick;
    float scrollDy;
    float startY;
    public SimpleCallback callback;
    public StoryModel storyModel;
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    MomentAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.story_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.storyFragment);
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        startY = size.y;
        view.setY(startY);

        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });


        return view;
    }

    public void setStoryModel(StoryModel story) {
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        storyModel = story;
        Log.v("USER", "user : " + story.getUserModel().getUsername());
        storyModel.fetch(new SimpleCallback() {
            @Override
            public void callbackCall() {
                initialize();
                mDemoCollectionPagerAdapter.setMoments(storyModel.getMoments());
                // mDemoCollectionPagerAdapter.getItem(mViewPager.getCurrentItem());
            }
        });
    }

    public void initialize() {
        mDemoCollectionPagerAdapter = new MomentAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                mViewPager.onTouchEvent(event);
                return true;
            }
        });
    }

    public void handleTouch(MotionEvent ev) {
        float threshold = 2.0f;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                touchActionDownX = (int) ev.getX();
                touchActionDownY = (int) ev.getY();
                touchActionMoveStatus = true;
                //cameraFragment.startMove(ev.getRawX());
                //wrapperDx = wrapper.getX() - ev.getRawX();
                //menuFragment.startMove(ev.getRawX());
                scrollDy = getView().getY() - ev.getRawY();
                lastX = ev.getRawY();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                touchActionMoveStatus = true;
                break;

            case MotionEvent.ACTION_MOVE:
                isClick = false;
                if (touchActionMoveStatus) {
                    touchActionMoveX = (int) ev.getX();
                    touchActionMoveY = (int) ev.getY();


                    float ratioLeftRight = Math.abs(touchActionMoveX - touchActionDownX) / Math.abs(touchActionMoveY - touchActionDownY);
                    float ratioUpDown = Math.abs(touchActionMoveY - touchActionDownY) / Math.abs(touchActionMoveX - touchActionDownX);

                    if (touchActionMoveX < touchActionDownX && ratioLeftRight > threshold) {
                        Log.i("test", "Move Left");
                        direction = DirectionHelper.LEFT;
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveX > touchActionDownX && ratioLeftRight > threshold) {
                        Log.i("test", "Move Right");
                        direction = DirectionHelper.RIGHT;
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveY < touchActionDownY && ratioUpDown > threshold) {
                        Log.i("test", "Move Up");
                        direction = DirectionHelper.UP;
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveY > touchActionDownY && ratioUpDown > threshold) {
                        Log.i("test", "Move Down");
                        direction = DirectionHelper.DOWN;
                        touchActionMoveStatus = false;
                    }
                }
                move(ev);
                //check whether the user changed scroll direction while dragging
                if (lastX > ev.getRawY()) {
                    lastX = ev.getRawY();
                    lastScrolledUp = true;
                } else {
                    lastX = ev.getRawY();
                    lastScrolledUp = false;
                }

                break;
            case MotionEvent.ACTION_UP: {
                if (isClick) {
                    Log.v("IS CLICK", "CLICK CLIKC");
                    if (mViewPager.getCurrentItem() == storyModel.getMoments().size() - 1) {
                        animateOut();
                    }
                    else{
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
                    }


                } else {
                    if (direction == DirectionHelper.DOWN || direction == DirectionHelper.UP) {
                        if (lastScrolledUp) {
                            animateIn();
                        } else {
                            animateOut();
                        }
                    }
                }

                break;
            }
        }
    }

    public void move(MotionEvent m) {
        float result = m.getRawY() + scrollDy;
        if (direction == DirectionHelper.DOWN || direction == DirectionHelper.UP) {
            if (result > 0) {
                getView().setY(result);
            } else {
                getView().setY(0);
            }
        }
    }

    public void animateIn() {
        view.animate().translationY(0).setDuration(250);
    }

    public void animateOut() {
        view.animate().translationY(startY).setDuration(250);
        callback.callbackCall();
    }


}
