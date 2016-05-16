package no.twomonkeys.sneek.app.components.story;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import no.twomonkeys.sneek.app.shared.views.IndicatorView;

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
    public int previousPageIndex;
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    MomentAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    IndicatorView indicatorView;

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

        indicatorView = (IndicatorView) view.findViewById(R.id.storyIndicatorView);

        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });


        return view;
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
                    } else {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
                    }


                } else {
                    if (direction == DirectionHelper.DOWN || direction == DirectionHelper.UP) {
                        if (lastScrolledUp) {
                            animateIn(null);
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


    public void animateIn(final SimpleCallback scb) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Do something.
                mViewPager.setVisibility(View.VISIBLE);
                if (scb != null){
                    scb.callbackCall();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(250).start();

        //view.animate().translationY(0).setDuration(250);
    }

    public void animateOut() {
        MomentFragment momentFragment = mDemoCollectionPagerAdapter.getFragment(mViewPager.getCurrentItem());
        if (momentFragment != null) {
            momentFragment.stopVideo();
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", startY);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Do something.
                mViewPager.setVisibility(View.INVISIBLE);
                callback.callbackCall();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(250).start();
    }

    public void setStoryModel(final StoryModel story) {
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        initialize();
        animateIn(new SimpleCallback() {
            @Override
            public void callbackCall() {
                storyModel = story;
                Log.v("USER", "user : " + story.getUserModel().getUsername());
                storyModel.fetch(new SimpleCallback() {
                    @Override
                    public void callbackCall() {
                        mDemoCollectionPagerAdapter.setMoments(storyModel.getMoments());
                        updateProgressWithPercent();
                        // mDemoCollectionPagerAdapter.getItem(mViewPager.getCurrentItem());
                    }
                });
            }
        });
    }

    public void updateProgressWithPercent()
    {
        float pageCount = storyModel.getMoments().size();
        //Log.v("PAGE","page " + mViewPager.getCurrentItem() + " " + pageCount);
        float currentPage = (mViewPager.getCurrentItem() + 1);
        float percent = (currentPage/pageCount)*100;
        indicatorView.updateProgress(percent);
    }

    public void update(float v, int currentItem)
    {
        float pageCount = storyModel.getMoments().size();
        //Log.v("PAGE","page " + mViewPager.getCurrentItem() + " " + pageCount);
        float currentPage = currentItem + v;
        if (currentPage <= mViewPager.getCurrentItem() + 2)
        {
            float percent = (currentPage/pageCount)*100;


            indicatorView.updateProgress(percent);
        }
    }

    public void initialize() {
        mDemoCollectionPagerAdapter = new MomentAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setVisibility(View.INVISIBLE);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                //updateProgressWithPercent();

                update(v, i + 1);
            }

            @Override
            public void onPageSelected(int i) {
                previousPageIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                mViewPager.onTouchEvent(event);
                return true;
            }
        });
    }


}
