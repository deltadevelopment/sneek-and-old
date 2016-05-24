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
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.util.DisplayMetrics;
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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.Camera.CameraFragment;
import no.twomonkeys.sneek.app.components.Camera.VideoPlayBackFragment;
import no.twomonkeys.sneek.app.components.feed.FeedAdapter;
import no.twomonkeys.sneek.app.components.feed.TopBarFragment;
import no.twomonkeys.sneek.app.components.menu.MenuFragment;
import no.twomonkeys.sneek.app.components.story.StoryFragment;
import no.twomonkeys.sneek.app.shared.Callback;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.VideoHelper;
import no.twomonkeys.sneek.app.shared.models.StoryModel;
import no.twomonkeys.sneek.app.shared.views.BoolCallback;
import no.twomonkeys.sneek.app.shared.views.SneekVideoView;

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
    private boolean menuWasVisible;
    private boolean storyIsVisible;
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
    boolean canSingleTap;
    SneekVideoView videoView;
    View loadView;
    private MediaController mediaControls;
    private MediaPlayer mediaPlayer;
    private int mPlayerPosition;
    VideoHelper videoHelper;


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

        initConfiguration();

        setContentView(R.layout.activity_main);

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.root);
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
        cameraFragment.onCancelClb = new SimpleCallback() {
            @Override
            public void callbackCall() {
                animateCameraOut();
            }
        };
        cameraFragment.onLockClb = new BoolCallback() {
            @Override
            public void callbackCall(boolean bool) {
                lockMode = bool;
            }
        };
        final MainActivity self = this;
        cameraFragment.videoDone = new CameraFragment.VideoDoneCallback() {
            @Override
            public void onRecorded(File file) {

                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });


            }
        };

        topBar = (TopBarFragment) fragmentManager.findFragmentById(R.id.topBarFragment);
        topBar.onMoreClb = new SimpleCallback() {
            @Override
            public void callbackCall() {
                animateIn();
            }
        };
        topBar.onCameraClb = new SimpleCallback() {
            @Override
            public void callbackCall() {
                animateCameraIn();
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
                        refreshItems();
                        break;
                    }
                    case 1: {
                        refreshItems();
                        break;
                    }
                    case 2: {
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

        setup();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dy;
                mPrevY -= dy;
                if (!isRefreshing) {
                    topBar.drag(mPrevY, recyclerView.computeVerticalScrollOffset());
                }
            }
        });
        refreshItems();

        final File f = new File("/storage/emulated/0/Pictures/MyCameraApp/VID_20160524_130304.mp4");

        loadView = (View) findViewById(R.id.loadView2);
        videoView = (SneekVideoView) findViewById(R.id.videoSneekVideoView2);

        loadView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        videoView.setMinimumWidth(width);
        videoView.setMinimumHeight(height);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath(f.getAbsolutePath());
        videoView.start();
    }

    public void loadVideo() {
        //Log.v("Got here", "hello there " + momentModel.getMedia_url());

        //set the media controller buttons
        videoView.setVisibility(View.VISIBLE);
        if (mediaControls == null) {
            mediaControls = new MediaController(this);

        }
        // videoView.setMediaController(mediaControls);
        //Uri uri = Uri.parse(momentModel.getMedia_url());

        //videoView.setVideoURI(uri);
        //videoView.requestFocus();
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
                videoHelper.setMediaPlayer(mediaPlayer);
                mp.setLooping(true);
                //loadingView.stopAnimation();

                videoView.start();

            }
        });

        videoHelper = new VideoHelper(videoView, null, this);
        videoHelper.loadVideo();

    }


    public void initConfiguration() {
        Fresco.initialize(this);
        DataHelper.setContext(this);
        DataHelper.setMa(this);
        //Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Remove top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                    Log.v("MENU", "IS menu visible?" + menuIsVisible + " " + cameraIsVisible);
                    if (!menuIsVisible && !cameraIsVisible) {
                        StoryModel storyModel = feedAdapter.getFeedModel().getStories().get(recyclerView.getChildPosition(child));
                        presentStory(storyModel);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    void presentStory(StoryModel storyModel) {
        if (!lockMode) {
            storyFragment = storyFragment();
            storyFragment.setStoryModel(storyModel);
            lockMode = true;
            storyIsVisible = true;
            canScroll = false;
        }
    }


    public StoryFragment storyFragment() {
        storyFragment = (StoryFragment) fragmentManager.findFragmentById(R.id.storyFragment);
        storyFragment.callback = new SimpleCallback() {
            @Override
            public void callbackCall() {
                storyIsVisible = false;
                lockMode = false;
            }
        };
        return storyFragment;
    }

    public void refreshDrag(MotionEvent m) {
        if (recyclerView.computeVerticalScrollOffset() == 0) {
            isRefreshing = false;
            float result = m.getRawY() + scrollDy;
            float result2 = ((result / 300) * 100) * 1.8f;
            float percentage = (result / 300);

            homeBtn.setAlpha(1 - percentage);
            placeholderBtn.setAlpha(percentage);

            float endResult = result - result2;

            if (endResult < 300 && result >= 0) {
                recyclerView.setY(endResult);
            }
        }
    }

    public void stopRefreshDrag() {
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

    //Movements

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (storyIsVisible || lockMode) {

        } else {
            View v = getCurrentFocus();
            float threshold = 2.0f;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    direction = Direction.DOWN;
                    touchActionDownX = (int) ev.getX();
                    touchActionDownY = (int) ev.getY();
                    touchActionMoveStatus = true;

                    cameraFragment.startMove(ev.getRawX());
                    wrapperDx = wrapper.getX() - ev.getRawX();
                    menuFragment.startMove(ev.getRawX());
                    scrollDy = recyclerView.getY() - ev.getRawY();
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


                        float ratioLeftRight = Math.abs(touchActionMoveX - touchActionDownX) / Math.abs(touchActionMoveY - touchActionDownY);
                        float ratioUpDown = Math.abs(touchActionMoveY - touchActionDownY) / Math.abs(touchActionMoveX - touchActionDownX);

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
                        if (canSingleTap) {
                            Log.v("SINGLE CLICK", "SINGLE CLICK");
                            if (menuIsVisible) {
                                Log.v("Animating ", " OUUTUT");
                                animateOut();
                            } else if (cameraIsVisible) {
                                animateCameraOut();
                            }
                        }
                    } else {
                        stopMovement(ev);
                    }

                    break;
                }
            }

        }
        return super.dispatchTouchEvent(ev);
    }


    void stopMovement(MotionEvent m) {
        stopRefreshDrag();
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
        refreshDrag(m);
    }

    //Animations

    private void animateIn() {
        ColorDrawable cd = new ColorDrawable(0xFF000000);
        cd.setAlpha(calculate(100));
        overlayShadow.setBackgroundDrawable(cd);

        // refreshSwipeLayout.setEnabled(false);

        menuFragment.animateIn();
        topBar.animateOut();
        homeBtn.animate().alpha(0).setDuration(150);
        cameraFragment.stopCamera();
        menuIsVisible = true;
    }

    private void animateOut() {
        cameraFragment.stopCamera();
        ColorDrawable cd = new ColorDrawable(0xFF000000);
        cd.setAlpha(calculate(0));
        overlayShadow.setBackgroundDrawable(cd);
        //refreshSwipeLayout.setEnabled(true);

        menuFragment.animateOut();
        topBar.animateIn();
        //homeBtn.animate().alpha(255).setDuration(150);

        ObjectAnimator anim = ObjectAnimator.ofFloat(homeBtn, "alpha", 255);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                menuIsVisible = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(150).start();


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
        canSingleTap = false;
    }

    private void animateCameraOut() {
        cameraFragment.stopCamera();
        // refreshSwipeLayout.setEnabled(true);
        cameraIsVisible = false;
        cameraFragment.animateOut();
        wrapper.animate().translationX(0).setDuration(150);
        canSingleTap = true;
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
}
