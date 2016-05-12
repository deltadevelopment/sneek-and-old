package no.twomonkeys.sneek.app.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.Camera.CameraFragment;
import no.twomonkeys.sneek.app.components.feed.FeedAdapter;
import no.twomonkeys.sneek.app.components.feed.TopBarFragment;
import no.twomonkeys.sneek.app.components.menu.MenuFragment;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private float mPrevY;
    private TopBarFragment topBar;
    private MenuFragment menuFragment;
    private CameraFragment cameraFragment;
    private float x1, y1, y2, x2, dx, dy;
    private boolean showing;
    private static long mDeBounce = 0;
    private boolean isClick;
    private boolean dragY;
    private boolean canScroll;
    private int overallXScroll = 0;

    private boolean isScrolling;
    private boolean menuIsVisible;
    private boolean cameraIsVisible;
    private ImageButton homeBtn;
    private ImageButton placeholderBtn;
    private SwipeRefreshLayout refreshSwipeLayout;
    private VelocityTracker mTracker;

    private boolean cameraSwipe;
    private GestureLibrary gestureLib;
    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG = "Gestures";
    private boolean doOnce;
    private Date oldDate;

    FrameLayout overlayShadow;

    RelativeLayout wrapper;
    float wrapperDx;
    float scrollDy;
    boolean shouldAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataHelper.setContext(this);
        //Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //remove top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Layout

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.root);
        wrapper = (RelativeLayout) findViewById(R.id.contentWrapper);


        overlayShadow = (FrameLayout) findViewById(R.id.overlayShadow);
        homeBtn = (ImageButton) findViewById(R.id.homeButton);
        homeBtn.setImageResource(R.drawable.triangle);
        placeholderBtn = (ImageButton) findViewById(R.id.placeholderButton);
        placeholderBtn.setImageResource(R.drawable.triangle);
        placeholderBtn.setColorFilter(Color.parseColor("#27ffff"));

        FragmentManager fragmentManager = getSupportFragmentManager();

        //Object initialization
        menuFragment = (MenuFragment) fragmentManager.findFragmentById(R.id.menuFragment);
        cameraFragment = (CameraFragment) fragmentManager.findFragmentById(R.id.cameraFragment);
/*
        menuFragment.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                recyclerView.onTouchEvent(m);
                recyclerView.setNestedScrollingEnabled(false);
                handleTouch(m, v);
                return true;
            }

        });
*/

        topBar = (TopBarFragment) fragmentManager.findFragmentById(R.id.topBarFragment);
        topBar.callback = new Callback() {
            @Override
            public void callbackCall() {
                animateIn();
            }

            @Override
            public void callbackCall(int row) {

            }
        };
        topBar.callback2 = new Callback() {
            @Override
            public void callbackCall() {
                cameraFragment.prepareCamera();
                animateCameraIn();
            }

            @Override
            public void callbackCall(int row) {

            }
        };

        menuFragment.callback = new Callback() {
            @Override
            public void callbackCall() {

            }

            @Override
            public void callbackCall(int row) {
                animateOut();
                switch (row) {
                    case 0: {
                        //Go to randoms feed
                        refreshItems();
                        break;
                    }
                    case 1: {
                        //Go to stalking feed
                        refreshItems();
                        break;
                    }
                    case 2: {
                        //Go to FML, settings
                        break;
                    }
                }
            }
        };

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycleView);
        feedAdapter = new FeedAdapter(this);
        recyclerView.setAdapter(feedAdapter);

        setup();
        canScroll = false;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return canScroll;
            }
        };

        recyclerView.setLayoutManager(gridLayoutManager);


        menuFragment.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                recyclerView.onTouchEvent(m);
                recyclerView.setNestedScrollingEnabled(false);
                handleTouch(m, v);
                return true;
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.v("TAGGY", "Tried");

                isScrolling = true;


                //mPrevY = 0;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dy;

                mPrevY -= dy;
                topBar.drag(mPrevY, recyclerView.computeVerticalScrollOffset());
            }
        });

        StoryModel storyModel = new StoryModel();

        refreshItems();
    }


    //Refreshing
    void refreshItems() {
        recyclerView.animate().translationY(300).setDuration(150);
        homeBtn.animate().alpha(0).setDuration(150);
        placeholderBtn.animate().alpha(255).setDuration(150);
        shouldAnimate = true;
        animateLoader();

        feedAdapter.updateData(new SimpleCallback() {
            @Override
            public void callbackCall() {
                recyclerView.animate().translationY(0).setDuration(150);
                homeBtn.animate().alpha(255).setDuration(150);
                placeholderBtn.animate().alpha(0).setDuration(150);
                placeholderBtn.setColorFilter(Color.parseColor("#27ffff"));
                shouldAnimate = false;
            }
        });
    }

    void animateLoader() {
        final String cyan = "#27ffff";
        String magenta = "#fa00fa";
        final String yellow = "#ffff00";
        final String black = "#000000";

        animateToColor(magenta, new SimpleCallback() {
            @Override
            public void callbackCall() {
                animateToColor(yellow, new SimpleCallback() {
                    @Override
                    public void callbackCall() {
                        animateToColor(black, new SimpleCallback() {
                            @Override
                            public void callbackCall() {
                                animateToColor(cyan, new SimpleCallback() {
                                    @Override
                                    public void callbackCall() {
                                        animateLoader();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    public void animateToColor(String color, final SimpleCallback scb) {

        if (shouldAnimate) {
            ObjectAnimator anim = ObjectAnimator.ofInt(placeholderBtn, "colorFilter", Color.parseColor(color));
            anim.setInterpolator(new LinearInterpolator());
            anim.setInterpolator(new AccelerateInterpolator());

            anim.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Do something.
                    if (shouldAnimate) {
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
        }
    }


    void changeOpacity() {
        if (menuFragment.shouldChangeColor()) {
            ColorDrawable cd = new ColorDrawable(0xFF000000);
            cd.setAlpha(menuFragment.percentageColor());
            overlayShadow.setBackgroundDrawable(cd);
        }
    }

    public void setup() {
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    //Drawer.closeDrawers();
                    if (!menuIsVisible) {
                        Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    }

                    //menuFragment.animateOut();
                    animateOut();
                    return true;

                }


                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.v("TEST", "drag");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    void handleTouch(MotionEvent m, View v) {
        //final int action = m.getAction();
        int index = m.getActionIndex();
        int action = m.getActionMasked();
        int pointerId = m.getPointerId(index);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (mTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mTracker.clear();
                }
                // Add a user's movement to the tracker.
                mTracker.addMovement(m);

                x1 = m.getX();
                y1 = m.getY();
                isClick = true;
                menuFragment.startMove(m.getRawX());
                cameraFragment.startMove(m.getRawX());
                wrapperDx = wrapper.getX() - m.getRawX();
                scrollDy = recyclerView.getY() - m.getRawX();
                isScrolling = false;
                canScroll = true;
                doOnce = false;
                oldDate = new Date();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mTracker.addMovement(m);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                float velX = VelocityTrackerCompat.getXVelocity(mTracker, pointerId);
                float velY = VelocityTrackerCompat.getYVelocity(mTracker, pointerId);
                //was here

                isClick = false;
                x2 = m.getX();
                y2 = m.getY();
                dx = x2 - x1;
                dy = y2 - y1;
                dragY = draggingY();
                mTracker.addMovement(m);

                long diffInMs = new Date().getTime() - oldDate.getTime();
                long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
                if (diffInMs < 100) {
                    //Maybe also check y here?? so we can use this to see if the user drags in a menu or just scrolls
                    if (velX > 0) {
                        cameraSwipe = false;
                    } else {
                        cameraSwipe = true;
                        if (!menuIsVisible && !cameraIsVisible) {
                            cameraFragment.prepareCamera();
                        }
                        //prepare camera here
                    }
                }

                if (!isScrolling) {
                    if (shouldMoveMenu()) {
                        moveMenu(m);
                    } else {
                        moveCamera(m);
                    }
                    canScroll = false;
                }

                if (dragY) {
                    canScroll = false;
                    // Use dx and dy to determine the direction
                } else {
                    if (!menuIsVisible && !cameraIsVisible) {
                        canScroll = true;
                        if (velY > 0) {
                            refreshTry(m);
                        }

                    }
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.v("UP", "ACTIONUP");
                if (shouldMoveMenu()) {
                    stopMoveMenu();
                } else {
                    stopMoveCamera();
                }
                stopRefreshTry();
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                Log.v("CANCELED", "CANCELED");
                // Return a VelocityTracker object back to be re-used by others.
                if (shouldMoveMenu()) {
                    stopMoveMenu();
                } else {
                    stopMoveCamera();
                }
                stopRefreshTry();
                mTracker.recycle();
                mTracker = null;
                break;
        }
    }

    public void refreshTry(MotionEvent m) {
        if (recyclerView.computeVerticalScrollOffset() == 0) {
            float result = m.getRawY() + scrollDy;
            float result2 = ((result / 300) * 100) * 1.8f;
            float percentage = (result / 300);

            homeBtn.setAlpha(1 - percentage);
            placeholderBtn.setAlpha(percentage);

            float endResult = result - result2;
            Log.v("RESULT", "res " + endResult + " ::: " + result2);

            if (endResult < 300 && result >= 0) {
                recyclerView.setY(endResult);
            }
        }
    }

    public void stopRefreshTry() {
        if (recyclerView.getY() > 0) {
            refreshItems();
        }
    }

    public boolean shouldMoveMenu() {
        if (cameraSwipe) {
            if (menuIsVisible) {
                return true;
            } else if (cameraIsVisible) {
                return false;
            } else {
                return false;
            }
        } else {
            if (menuIsVisible) {
                return true;
            } else if (cameraIsVisible) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void moveMenu(MotionEvent m) {
        menuFragment.dragRight(m.getRawX());
        changeOpacity();
        updateAlpha(menuFragment.percentageScrolled());
    }

    public void moveCamera(MotionEvent m) {
        Log.v("camera", "move");
        cameraFragment.drag(m.getRawX());

        float result = m.getRawX() + wrapperDx;


        wrapper.setX(result);
    }

    public void stopMoveMenu() {
        if (isClick) {
            Log.v("s", "s");
            if (menuFragment.isShowing()) {
                //menuFragment.show();
                animateOut();
            }
            //menuFragment.animateOut();
        } else if (!isScrolling) {
            if (menuFragment.isShowing()) {
                animateOut();
            } else {
                animateIn();
            }
            //menuFragment.show();
        } else {
            //menuFragment.animateOut();
            animateOut();
        }
    }

    public void stopMoveCamera() {
        if (isClick) {


            //menuFragment.animateOut();
        } else if (!isScrolling) {
            if (cameraIsVisible) {
                animateCameraOut();
            } else {
                animateCameraIn();
            }
            //menuFragment.show();
        } else {
            //menuFragment.animateOut();
            animateCameraOut();
        }
    }

    public void updateAlpha(float alpha) {
        homeBtn.setAlpha(1 - alpha);
        topBar.updateAlpha(1 - alpha);
    }

    public boolean draggingY() {
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                return true;
            } else {
                return true;
            }
        } else {
            if (dy > 0) {
                return false;
            } else {
                return false;
            }
        }
    }

    public void goToTop(View v) {
        Log.v(TAG, "Clicked to top");
        recyclerView.smoothScrollToPosition(0);
    }

    //Animations

    private void animateIn() {
        ColorDrawable cd = new ColorDrawable(0xFF000000);
        cd.setAlpha(calculate(100));
        overlayShadow.setBackgroundDrawable(cd);

        // refreshSwipeLayout.setEnabled(false);
        menuIsVisible = true;
        menuFragment.animateIn();
        topBar.animateOut();
        homeBtn.animate().alpha(0).setDuration(150);
        cameraFragment.stopCamera();
    }

    private void animateOut() {
        cameraFragment.stopCamera();
        ColorDrawable cd = new ColorDrawable(0xFF000000);
        cd.setAlpha(calculate(0));
        overlayShadow.setBackgroundDrawable(cd);
        //refreshSwipeLayout.setEnabled(true);
        menuIsVisible = false;
        menuFragment.animateOut();
        topBar.animateIn();
        homeBtn.animate().alpha(255).setDuration(150);
    }

    int calculate(int percentage) {
        int bigAlpha = 128;
        int result = (percentage * bigAlpha) / 100;
        return result;
    }

    private void animateCameraIn() {
        // refreshSwipeLayout.setEnabled(false);
        cameraIsVisible = true;
        cameraFragment.animateIn();
        //wrapper.setX(-wrapper.getWidth());
        wrapper.animate().translationX(-wrapper.getWidth()).setDuration(150);
    }

    private void animateCameraOut() {
        cameraFragment.stopCamera();
        // refreshSwipeLayout.setEnabled(true);
        cameraIsVisible = false;
        cameraFragment.animateOut();
        wrapper.animate().translationX(0).setDuration(150);
    }
}
