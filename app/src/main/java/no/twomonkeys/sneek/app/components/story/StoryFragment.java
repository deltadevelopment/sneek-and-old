package no.twomonkeys.sneek.app.components.story;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.MainActivity;
import no.twomonkeys.sneek.app.components.menu.MoreButton;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.DateHelper;
import no.twomonkeys.sneek.app.shared.helpers.DirectionHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StalkModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;
import no.twomonkeys.sneek.app.shared.views.IndicatorView;
import no.twomonkeys.sneek.app.shared.views.MoreView;

/**
 * Created by simenlie on 13.05.16.
 */
public class StoryFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "StoryFragment";
    View view;
    DirectionHelper direction;
    float touchActionDownX, touchActionDownY, touchActionMoveX, touchActionMoveY, lastX;
    boolean touchActionMoveStatus, lastScrolledUp, isClick;
    float scrollDy;
    float startY;
    public SimpleCallback2 callback;
    public StoryModel storyModel;
    public int previousPageIndex;
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    MomentAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    IndicatorView indicatorView;
    Button moreBtn;
    MoreView moreView;
    MoreViewModel viewModel;
    MomentFragment currentMomentFragment;

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

        moreView = (MoreView) view.findViewById(R.id.momentMoreView);
        moreView.setAlpha(0.0f);
        moreView.onStalkUser = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                if (viewModel.isStalkingUser) {
                    unstalkUser();
                } else {
                    stalkUser();
                }
            }
        };
        moreView.onBlock = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                showBlockAlert();
            }
        };

        moreView.onDelete = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                deleteMoment();
            }
        };
        moreView.onDeleteAll = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                deleteAllMoments();
            }
        };

        moreView.onStalkStream = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                stalkStream();
            }
        };

        moreView.onHidden = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                moreBtn.setVisibility(View.VISIBLE);
                currentMomentFragment.usernameTxt.setVisibility(View.VISIBLE);
                currentMomentFragment.updatedTxt.setVisibility(View.VISIBLE);
                indicatorView.setVisibility(View.VISIBLE);
                moreView.setVisibility(View.INVISIBLE);
            }
        };

        moreView.onReport = new SimpleCallback2() {
            @Override
            public void callbackCall() {
                report();
            }
        };

        moreBtn = (Button) view.findViewById(R.id.momentMoreBtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreView.getAlpha() == 1.0f) {
                    moreView.animateOut();
                } else {
                    showMore();
                }

            }
        });

        layoutBtn();
        viewModel = new MoreViewModel();
        moreView.setViewModel(viewModel);
        moreView.setVisibility(View.INVISIBLE);

        return view;
    }

    public void layoutBtn() {
        moreBtn.setBackgroundColor(getResources().getColor(R.color.black));
        moreBtn.setTextColor(getResources().getColor(R.color.white));

        moreBtn.setTypeface(Typeface.create("HelveticaNeue", 0));

        moreBtn.setText("MORE");
        int margin = UIHelper.dpToPx(getContext(), 10);
        int btnHeight = UIHelper.dpToPx(getContext(), 30);

        moreBtn.setPadding(margin, 0, margin, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.height = btnHeight;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0, margin, margin, margin);

        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(moreBtn.getTypeface());// your preference here
        paint.setTextSize(moreBtn.getTextSize());// have this the same as your text size

        String text = moreBtn.getText().toString();

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height = bounds.height();
        text_width = bounds.width() + (margin * 2) + 10;
        params.width = text_width;
        moreBtn.setLayoutParams(params);
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


    public void animateIn(final SimpleCallback2 scb) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Do something.
                mViewPager.setVisibility(View.VISIBLE);
                if (scb != null) {
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
            Log.v(TAG, "Stopping here");
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
        animateIn(new SimpleCallback2() {
            @Override
            public void callbackCall() {
                storyModel = story;
                storyModel.fetch(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        mDemoCollectionPagerAdapter.setMoments(storyModel.getMoments());
                        updateProgressWithPercent();
                        // mDemoCollectionPagerAdapter.getItem(mViewPager.getCurrentItem());
                    }
                });
            }
        });
    }


    public void updateProgressWithPercent() {
        float pageCount = storyModel.getMoments().size();
        //Log.v("PAGE","page " + mViewPager.getCurrentItem() + " " + pageCount);
        float currentPage = (mViewPager.getCurrentItem() + 1);
        float percent = (currentPage / pageCount) * 100;
        indicatorView.updateProgress(percent);
    }

    public void update(float v, int currentItem) {
        float pageCount = storyModel.getMoments().size();
        //Log.v("PAGE","page " + mViewPager.getCurrentItem() + " " + pageCount);
        float currentPage = currentItem + v;
        if (currentPage <= mViewPager.getCurrentItem() + 2) {
            float percent = (currentPage / pageCount) * 100;


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

    public void showMore() {
        moreView.setVisibility(View.VISIBLE);
        currentMomentFragment = mDemoCollectionPagerAdapter.getFragment(mViewPager.getCurrentItem());
        MomentModel currentMoment = currentMomentFragment.getMomentModel();

        moreBtn.setVisibility(View.INVISIBLE);
        currentMomentFragment.usernameTxt.setVisibility(View.INVISIBLE);
        currentMomentFragment.updatedTxt.setVisibility(View.INVISIBLE);
        indicatorView.setVisibility(View.INVISIBLE);

        // 0 = user
        // 1 = user_device
        // 2 = stream with device user
        // 3 = stream without device user
        viewModel.momentId = currentMoment.id;
        if (storyModel.getStream_type() != null) {
            //is stream
            viewModel.streamTitle = storyModel.getName();
            viewModel.username = currentMoment.getUserModel().getUsername();
            viewModel.userId = currentMoment.getUserModel().getId();
            viewModel.stalkersCountTxt = storyModel.stalkers_count;
            viewModel.isBlocked = DataHelper.isBlocked(currentMoment.getUserModel().getId());
            viewModel.isStalkingStream = DataHelper.hasTag(storyModel.getName());
            if (currentMoment.getUserModel().getId() == DataHelper.getUserId()) {
                //Is user moment
                moreView.setup(2);
            } else {
                moreView.setup(3);
                viewModel.isStalkingUser = currentMoment.getUserModel().is_following();
            }
        } else {
            //Is not stream
            Log.v("username is", "user is " + storyModel.getUserModel().getUsername());
            viewModel.userId = storyModel.getUserModel().getId();
            viewModel.username = storyModel.getUserModel().getUsername();
            viewModel.isBlocked = DataHelper.isBlocked(storyModel.getUserModel().getId());
            if (storyModel.getUserModel().getId() == DataHelper.getUserId()) {
                //Is user story
                moreView.setup(1);
            } else {
                viewModel.isStalkingUser = storyModel.is_following;
                moreView.setup(0);
            }
        }
        moreView.updateView();
        moreView.animateIn();
        moreView.animate().alpha(1.0f);
    }

    private StalkModel obtainStalkUserModel() {
        StalkModel stalkModel = new StalkModel();
        stalkModel.setUser_id(AuthHelper.getUserId());
        if (storyModel.getStream_type() != null) {
            stalkModel.setFollowee_id(currentMomentFragment.getMomentModel().getUserModel().getId());
        } else {
            stalkModel.setFollowee_id(storyModel.user_id);
        }
        return stalkModel;
    }

    private void stalkUser() {
        StalkModel stalkModel = obtainStalkUserModel();
        viewModel.isStalkingUser = true;
        moreView.updateView();
        stalkModel.saveUser(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                if (errorModel == null) {
                    storyModel.is_following = true;
                    UserModel userModel = currentMomentFragment.getMomentModel().getUserModel();
                    if (userModel != null) {
                        userModel.setIs_following(true);
                    }
                } else {
                    Log.v("Storyfragment", "FAILED TO STALK USER");
                }
            }
        });
    }

    private void unstalkUser() {
        StalkModel stalkModel = obtainStalkUserModel();
        viewModel.isStalkingUser = false;
        moreView.updateView();
        stalkModel.deleteUser(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {
                if (errorModel != null) {
                    storyModel.is_following = false;
                    UserModel userModel = currentMomentFragment.getMomentModel().getUserModel();
                    if (userModel != null) {
                        userModel.setIs_following(false);
                    }
                } else {
                    Log.v("Storyfragment", "FAILED TO STALK USER");
                }
            }
        });
    }

    private void stalkStream() {
        final StalkModel stalkModel = new StalkModel();
        stalkModel.setTag_name(storyModel.getName());
        final String tagId = DataHelper.tagId(storyModel.getName());
        if (tagId != null) {
            stalkModel.setStream_id(Integer.parseInt(tagId));
            viewModel.isStalkingStream = false;
            stalkModel.deleteStream(new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    DataHelper.removeTagStream(Integer.parseInt(tagId));
                    viewModel.stalkersCountTxt -= 1;
                    moreView.updateView();

                }
            });
        } else {
            viewModel.isStalkingStream = true;
            stalkModel.saveStream(new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    if (errorModel == null) {
                        DataHelper.storeTagStream(storyModel.id, storyModel.getName());
                        viewModel.stalkersCountTxt += 1;
                        moreView.updateView();
                    }
                }
            });
        }
    }

    private void showBlockAlert() {
        final UserModel userModel = new UserModel();
        userModel.setId(storyModel.user_id == 0 ? currentMomentFragment.getMomentModel().getUserModel().getId() : storyModel.user_id);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.block_user_h)
                .setMessage(R.string.block_user_b)
                .setPositiveButton(R.string.block_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        userModel.getBlockModel().save(new SimpleCallback() {
                            @Override
                            public void callbackCall(ErrorModel errorModel) {
                                if (errorModel == null) {
                                    viewModel.isBlocked = DataHelper.isBlocked(userModel.getId());
                                    moreView.updateView();
                                }
                            }
                        });


                    }
                })
                .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void deleteMoment() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_moment_title)
                .setMessage(R.string.delete_moment_msg)
                .setPositiveButton(R.string.ok_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteMomentAndUpdateUI();
                    }
                })
                .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void deleteMomentBEN(MomentModel momentModel) {
        currentMomentFragment.getMomentModel().delete(new SimpleCallback() {
            @Override
            public void callbackCall(ErrorModel errorModel) {

            }
        });
    }

    public void deleteMomentAndUpdateUI() {
        moreView.animateOut();
        currentMomentFragment.stopVideo();
        if (storyModel.getMoments().size() == 1) {
            animateOut();
            deleteMomentBEN(currentMomentFragment.getMomentModel());
            storyModel.popMoment(currentMomentFragment.getMomentModel());
        } else {
            int pageToGoTo = 0;
            if (mViewPager.getCurrentItem() == storyModel.getMoments().size() - 1) {
                //Direction reverse
                pageToGoTo = -1;
            } else {
                //Direction forward
                pageToGoTo = 0;
            }
            MomentModel oldMoment = currentMomentFragment.getMomentModel();
            deleteMomentBEN(oldMoment);
            storyModel.popMoment(oldMoment);
            mDemoCollectionPagerAdapter.setMoments(storyModel.getMoments());

            Log.v("current index ", "index " + mViewPager.getCurrentItem());
            int currentIndex = mViewPager.getCurrentItem() == 0 ? 0 : mViewPager.getCurrentItem() + pageToGoTo;
            currentMomentFragment = mDemoCollectionPagerAdapter.getFragment(currentIndex);

            mViewPager.setCurrentItem(currentIndex, true);
            //Go to new page. and then update progress
            //load video

        }
    }

    public void deleteAllMoments() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_all_moment_title)
                .setMessage(R.string.delete_all_moment_msg)
                .setPositiveButton(R.string.ok_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        MomentModel.deleteAll(new SimpleCallback() {
                            @Override
                            public void callbackCall(ErrorModel errorModel) {
                                moreView.animateOut();
                                animateOut();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void report() {

    }

    public class MoreViewModel {
        public String username, streamTitle, reportTxt;
        public int stalkersCountTxt;
        public boolean isStalkingStream, isStalkingUser, isBlocked;
        public int userId, momentId;

        public MoreViewModel() {

        }
    }
}
