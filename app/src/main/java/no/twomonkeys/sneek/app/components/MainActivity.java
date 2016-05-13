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
import android.support.v4.app.FragmentTransaction;
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

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.Camera.CameraFragment;
import no.twomonkeys.sneek.app.components.feed.FeedAdapter;
import no.twomonkeys.sneek.app.components.feed.TopBarFragment;
import no.twomonkeys.sneek.app.components.menu.MenuFragment;
import no.twomonkeys.sneek.app.components.story.StoryFragment;
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
    boolean lockMode;

    FrameLayout overlayShadow;

    RelativeLayout wrapper;
    float wrapperDx;
    float scrollDy;
    boolean shouldAnimate;
    StoryFragment storyFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;


    //New method
    float touchActionDownX, touchActionDownY, touchActionMoveX, touchActionMoveY;
    boolean touchActionMoveStatus;

    float lastX;
    boolean isRefreshing;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    Direction direction;
    boolean lastScrolledLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Fresco.initialize(this);
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

        fragmentManager = getSupportFragmentManager();

        //Object initialization
        menuFragment = (MenuFragment) fragmentManager.findFragmentById(R.id.menuFragment);
        cameraFragment = (CameraFragment) fragmentManager.findFragmentById(R.id.cameraFragment);
        storyFragment = (StoryFragment) fragmentManager.findFragmentById(R.id.storyFragment);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(storyFragment);
        fragmentTransaction.commit();

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


        canScroll = false;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return canScroll;
            }
        };

        recyclerView.setLayoutManager(gridLayoutManager);
        canScroll = true;
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

        setup();
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
                if (!isRefreshing){
                    Log.v("SCROLL","offset " + " " + mPrevY +  " " +  recyclerView.computeVerticalScrollOffset());
                    topBar.drag(mPrevY, recyclerView.computeVerticalScrollOffset());
                }

            }
        });

        refreshItems();
    }


    //Refreshing
    void refreshItems() {
        isRefreshing = true;
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
                isRefreshing = false;
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


                if (child != null) {
                    //Drawer.closeDrawers();
                    if (!menuIsVisible && mGestureDetector.onTouchEvent(motionEvent)) {
                        //Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                        Log.v("RECYCLERVIEW", "HITTING ? " + recyclerView.getChildPosition(child));
                        StoryModel storyModel = feedAdapter.getFeedModel().getStories().get(recyclerView.getChildPosition(child));
                        presentStory(storyModel);
                    }

                    //menuFragment.animateOut();
                    // animateOut();
                    return false;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        float threshold = 2.0f;
        // Log.v(TAG,"Touched " + v);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                touchActionDownX = (int) ev.getX();
                touchActionDownY = (int) ev.getY();
                touchActionMoveStatus = true;

                //Testing
                cameraFragment.startMove(ev.getRawX());
                wrapperDx = wrapper.getX() - ev.getRawX();
                menuFragment.startMove(ev.getRawX());
                scrollDy = recyclerView.getY() - ev.getRawY();
                //gameLoop.touchX = (int)ev.getX();
                //gameLoop.touchY = (int)ev.getY();
                // gameLoop.touchActionDown = true;
                lastX = ev.getRawX();
                break;

            case MotionEvent.ACTION_POINTER_UP:

                touchActionMoveStatus = true;

                break;

            case MotionEvent.ACTION_MOVE:
                isClick = false;
                if (touchActionMoveStatus) {
                    touchActionMoveX = (int) ev.getX();
                    touchActionMoveY = (int) ev.getY();

                    // I haven't tested this so you may have a few typos to correct.
                    float ratioLeftRight = Math.abs(touchActionMoveX - touchActionDownX) / Math.abs(touchActionMoveY - touchActionDownY);
                    float ratioUpDown = Math.abs(touchActionMoveY - touchActionDownY) / Math.abs(touchActionMoveX - touchActionDownX);

                    Log.v("hell","oo " + ratioLeftRight);

                    if (touchActionMoveX < touchActionDownX && ratioLeftRight > threshold) {
                        Log.i("test", "Move Left");
                        direction = Direction.LEFT;
                        canScroll = false;
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveX > touchActionDownX && ratioLeftRight > threshold) {
                        Log.i("test", "Move Right");
                        direction = Direction.RIGHT;
                        canScroll = false;
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveY < touchActionDownY && ratioUpDown > threshold) {
                        Log.i("test", "Move Up");
                        direction = Direction.UP;
                        if (!menuIsVisible && !cameraIsVisible) {
                            canScroll = true;
                        }
                        touchActionMoveStatus = false;
                    } else if (touchActionMoveY > touchActionDownY && ratioUpDown > threshold) {
                        Log.i("test", "Move Down");
                        direction = Direction.DOWN;
                        if (!menuIsVisible && !cameraIsVisible) {
                            canScroll = true;
                        }
                        touchActionMoveStatus = false;
                    }

                }
                //check whether the user changed scroll direction while dragging
                if (lastX > ev.getRawX()) {
                    lastX = ev.getRawX();
                    lastScrolledLeft = true;
                } else {
                    lastX = ev.getRawX();
                    lastScrolledLeft = false;
                }
                move(ev);
                break;
            case MotionEvent.ACTION_UP: {
                if (isClick) {
                    if (menuIsVisible) {
                        animateOut();
                    } else if (cameraIsVisible) {
                        animateCameraOut();
                    }
                } else {
                    stopMovement(ev);
                }

                break;
            }
        }


        return super.dispatchTouchEvent(ev);
    }

    void stopMovement(MotionEvent m) {
        stopRefreshTry();
        switch (direction) {
            case UP: {

                break;
            }
            case DOWN: {

                break;
            }
            case LEFT: {
                if (menuIsVisible) {
                    if (lastScrolledLeft) {
                        animateOut();
                    } else {
                        animateIn();
                    }

                } else {
                    if (lastScrolledLeft) {
                        animateCameraIn();
                    } else {
                        animateCameraOut();
                    }

                }
                break;
            }
            case RIGHT: {
                if (cameraIsVisible) {
                    if (lastScrolledLeft) {
                        animateCameraIn();
                    } else {
                        animateCameraOut();
                    }

                } else {
                    if (lastScrolledLeft) {
                        animateOut();
                    } else {
                        animateIn();
                    }

                }
                break;
            }
        }
    }


    void move(MotionEvent ev) {
        switch (direction) {
            case UP: {
                moveUp(ev);
                break;
            }
            case DOWN: {
                moveDown(ev);
                break;
            }
            case LEFT: {
                moveLeft(ev);
                break;
            }
            case RIGHT: {
                moveRight(ev);
                break;
            }
        }
    }

    void moveLeft(MotionEvent m) {
        if (!menuIsVisible) {
            moveCamera(m);
        } else {
            moveMenu(m);
        }
    }

    void moveRight(MotionEvent m) {
        if (!cameraIsVisible) {
            moveMenu(m);
        } else {
            moveCamera(m);
        }
    }

    void moveUp(MotionEvent motionEvent) {

    }

    void moveDown(MotionEvent m) {
        refreshTry(m);
    }

    void presentStory(StoryModel storyModel) {
        /*
        if (!lockMode) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(storyFragment);
            fragmentTransaction.commit();
            lockMode = true;
            storyFragment.animateIn();
        }
        */
    }


    public void refreshTry(MotionEvent m) {
        if (recyclerView.computeVerticalScrollOffset() == 0) {
            isRefreshing = false;
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

    public void moveMenu(MotionEvent m) {
        menuFragment.dragRight(m.getRawX());
        changeOpacity();
        updateAlpha(menuFragment.percentageScrolled());
    }

    public void moveCamera(MotionEvent m) {
        cameraFragment.drag(m.getRawX());

        float result = m.getRawX() + wrapperDx;

        if (result < 0) {
            wrapper.setX(result);
        }

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
