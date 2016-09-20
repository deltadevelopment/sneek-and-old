package no.twomonkeys.sneek.app.components.stalk;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.mp4parser.authoring.Edit;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.KeyboardUtil;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.helpers.UtilHelper;
import no.twomonkeys.sneek.app.shared.models.ErrorModel;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StalkModel;
import no.twomonkeys.sneek.app.shared.models.StreamModel;
import no.twomonkeys.sneek.app.shared.models.UserModel;
import no.twomonkeys.sneek.app.shared.views.BoolCallback;
import no.twomonkeys.sneek.app.shared.views.LoadingView;
import okhttp3.internal.Util;

/**
 * Created by simenlie on 13.06.16.
 */
public class StalkController extends RelativeLayout {
    private static final String TAG = "StalkController";
    Button stalkBtn;
    TextView noUsersTextView;
    EditText stalkEditText;
    Button stalkBackBtn;
    TextView stalkersCountText;
    LoadingView stalkLoadingView;
    public BoolCallback onLockClb;
    Timer searchTimer;
    boolean shouldFlash;
    StreamModel tagStreamModel;
    UserModel userFound;
    Activity activity;
    boolean hasStalked;
    ArrayList<String> flashSuggestions;
    Timer suggestionTimer;

    public StalkController(Context context) {
        super(context);
        initializeViews(context);
    }

    public StalkController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public StalkController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stalk_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        getStalkBtn();
        getNoUsersTextView();
        getStalkEditText();
        getStalkBackBtn();
        getStalkLoadingView();
        getStalkersCountText();
        setY(UIHelper.screenHeight(getContext()));
        setVisibility(INVISIBLE);

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
    }


    public void enableKeyboard(Activity activity) {
        this.activity = activity;
        KeyboardUtil keyboardUtil = new KeyboardUtil(activity, this);
        keyboardUtil.enable();
    }

    public void animateIn() {
        onLockClb.callbackCall(true);

        setVisibility(VISIBLE);

        if (DataHelper.flashSuggestions() == null) {
            suggestionTimer = new Timer();
            suggestionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkSuggestions();
                        }
                    });
                }
            }, 1000, 1000);
        } else {
            startFlash();
        }

        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", 0);
        UIHelper.animate(anim, 150, new SimpleCallback2() {
            @Override
            public void callbackCall() {
                (new Handler()).postDelayed(new Runnable() {

                    public void run() {
                        stalkEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                        stalkEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                    }
                }, 100);
            }
        });
    }

    public void startFlash() {
        flashSuggestions = DataHelper.flashSuggestions();
        stalkEditText.setHint(flashSuggestions.get(0));
        shouldFlash = true;
        flashSuggestions(1);
    }

    public void checkSuggestions() {
        if (DataHelper.flashSuggestions() != null) {
            suggestionTimer.cancel();
            startFlash();
        }
    }

    public void animateOut() {
        onLockClb.callbackCall(false);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(stalkEditText.getWindowToken(), 0);
        setVisibility(VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", UIHelper.screenHeight(getContext()));
        UIHelper.animate(anim, 150, new SimpleCallback2() {
            @Override
            public void callbackCall() {
                setVisibility(INVISIBLE);
            }
        });
    }

    private Button getStalkBtn() {
        if (stalkBtn == null) {
            Button stalkBtn = (Button) findViewById(R.id.stalkStalkBtn);
            stalkBtn.setVisibility(GONE);
            UIHelper.layoutBtn(getContext(), stalkBtn, "STALK");
            stalkBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stalkAction();
                }
            });
            this.stalkBtn = stalkBtn;
        }
        return stalkBtn;
    }

    private TextView getNoUsersTextView() {
        if (noUsersTextView == null) {
            TextView noUsersTextView = (TextView) findViewById(R.id.noUsersTextView);
            noUsersTextView.setVisibility(GONE);
            this.noUsersTextView = noUsersTextView;
        }
        return stalkBtn;
    }

    private TextView getStalkersCountText() {
        if (stalkersCountText == null) {
            TextView stalkersCountText = (TextView) findViewById(R.id.stalkersCountText);
            stalkersCountText.setVisibility(GONE);
            this.stalkersCountText = stalkersCountText;
        }
        return stalkersCountText;
    }

    private void textChanged() {
        if (searchTimer != null) {
            searchTimer.cancel();
        }

        stalkBtn.setVisibility(GONE);
        stalkersCountText.setVisibility(GONE);
        noUsersTextView.setVisibility(GONE);
        String searchText = stalkEditText.getText().toString();

        if (searchText.length() > 0) {
            shouldFlash = false;
            stalkEditText.setAlpha(1.0f);
            if (!isOnlyType()) {
                if (!stalkLoadingView.isAnimating()) {
                    stalkLoadingView.startAnimate();
                }

                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search();
                            }
                        });
                    }
                }, 2000);
            }
        } else {
            shouldFlash = true;
            flashSuggestions(0);
            stalkLoadingView.stopAnimation();
            noUsersTextView.setVisibility(INVISIBLE);
        }

        if (searchText.length() <= 16) {

        } else {
            if (searchIsTag()) {
                if (searchText.length() <= 20) {

                } else {
                    //return false
                }
            }
        }


    }

    private void stalkAction() {
        if (searchIsTag()) {
            final String tagName = stalkEditText.getText().toString().substring(1);
            final String tagid = DataHelper.tagId(tagName);
            final StalkModel stalkModel = new StalkModel();
            stalkModel.setTag_name(tagName);
            //Startstoploading
            if (tagid != null) {
                stalkModel.setStream_id(Integer.parseInt(tagid));
                stalkModel.deleteStream(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        DataHelper.removeTagStream(Integer.parseInt(tagid));
                        uiForStalking(DataHelper.hasTag(tagName));
                        tagStreamModel.setStalkers_count(tagStreamModel.getStalkers_count() - 1);
                        stalkersCountText.setText(UtilHelper.stalkersStringForNumber(tagStreamModel.getStalkers_count()));
                    }
                });
            } else {
                stalkModel.saveStream(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        if (errorModel == null) {

                            DataHelper.storeTagStream(stalkModel.getStream_id(), tagName);
                            uiForStalking(DataHelper.hasTag(tagName));
                            if (tagStreamModel == null) {
                                tagStreamModel = new StreamModel();
                                Log.v(TAG, "tag stream " + stalkModel.getStream_id() + " " + tagName + " " + tagStreamModel.getStalkers_count());
                            }

                            tagStreamModel.setStalkers_count(tagStreamModel.getStalkers_count() + 1);
                            stalkersCountText.setText(UtilHelper.stalkersStringForNumber(tagStreamModel.getStalkers_count()));

                            hasStalked = true;
                        }
                    }
                });
            }
        } else {
            StalkModel stalkModel = new StalkModel();
            stalkModel.setUser_id(AuthHelper.getUserId());
            stalkModel.setFollowee_id(userFound.getId());
            if (userFound.is_following()) {
                stalkModel.deleteUser(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        onStalking(errorModel);
                    }
                });
            } else {
                stalkModel.saveUser(new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        onStalking(errorModel);
                        hasStalked = true;
                    }
                });
            }
        }


    }

    private void onStalking(ErrorModel errorModel) {

        Log.v("YEP", "UPDATE UI HERE");
        userFound.setIs_following(!userFound.is_following());
        uiForStalking(userFound.is_following());

    }

    private void flashSuggestions(final int flashNumber) {
        if (shouldFlash && flashSuggestions != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(stalkEditText, "alpha", 0.0f);
            UIHelper.animate(animator, 400, 1000, new SimpleCallback2() {
                @Override
                public void callbackCall() {
                    stalkEditText.setHint(flashSuggestions.get(flashNumber));
                    ObjectAnimator animator2 = ObjectAnimator.ofFloat(stalkEditText, "alpha", 1.0f);
                    UIHelper.animate(animator2, 400, new SimpleCallback2() {
                        @Override
                        public void callbackCall() {
                            if (shouldFlash) {
                                int index2 = flashNumber + 1;
                                if (index2 >= flashSuggestions.size()) {
                                    index2 = 0;
                                }
                                flashSuggestions(index2);
                            }
                        }
                    });

                }
            });
        }

    }

    private boolean isOnlyType() {
        return false;
    }

    private EditText getStalkEditText() {
        if (stalkEditText == null) {
            EditText stalkEditText = (EditText) findViewById(R.id.stalkEditText);
            stalkEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Log.v(TAG, "TExt before changed");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Log.v(TAG, "TExt changed");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    textChanged();
                }
            });
            UIHelper.setCursorDrawableColor(stalkEditText, getResources().getColor(R.color.cyan));
            this.stalkEditText = stalkEditText;
        }
        return stalkEditText;
    }


    private Button getStalkBackBtn() {
        if (stalkBackBtn == null) {
            Button stalkBackBtn = (Button) findViewById(R.id.stalkBackBtn);
            UIHelper.layoutBtnRelative(getContext(), stalkBackBtn, "BACK");
            stalkBackBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateOut();
                }
            });
            this.stalkBackBtn = stalkBackBtn;
        }
        return stalkBackBtn;
    }

    private LoadingView getStalkLoadingView() {
        if (stalkLoadingView == null) {
            LoadingView stalkLoadingView = (LoadingView) findViewById(R.id.stalkLoadingView);
            stalkLoadingView.setVisibility(INVISIBLE);
            stalkLoadingView.removeBg();
            stalkLoadingView.blackTheme();
            this.stalkLoadingView = stalkLoadingView;
        }
        return stalkLoadingView;
    }


    private void search() {
        if (searchIsTag()) {
            searchForTag();
        } else {
            searchForUser();
        }
    }

    private boolean searchIsTag() {
        String searchTxt = stalkEditText.getText().toString();
        String searchTxtFirst = searchTxt.substring(0, 1);
        if (searchTxtFirst.equals("#")) {
            return true;
        } else {
            return false;
        }
    }

    private void searchForUser() {
        String stringStripped = stalkEditText.getText().toString().substring(1);
        String finalSearchString = stalkEditText.getText().toString();

        if (hasAtMention()) {
            finalSearchString = stringStripped;
        }
        if (stringStripped.length() == 0 && hasAtMention()) {
            noUsersTextView.setText("No users found");
            showNotValid();
        } else if (!searchTxtIsValid()) {
            noUsersTextView.setText("No users found");
            showNotValid();
        } else {
            UserModel.fetch(finalSearchString, new UserModel.UserModelCallback() {
                @Override
                public void callbackCall(UserModel userModel) {
                    userFound = userModel;
                    stalkLoadingView.stopAnimation();
                    if (userFound.getId() == AuthHelper.getUserId()) {
                        noUsersTextView.setText("Moron");
                        noUsersTextView.setVisibility(VISIBLE);
                    } else {
                        uiForStalking(userFound.is_following());
                        stalkBtn.setVisibility(VISIBLE);
                    }
                }
            }, new SimpleCallback() {
                @Override
                public void callbackCall(ErrorModel errorModel) {
                    if (errorModel != null) {
                        stalkLoadingView.stopAnimation();
                        if (errorModel.errorForKey("message_id") != null) {
                            //set messages error here
                        }
                        noUsersTextView.setText("no users txt");
                        noUsersTextView.setVisibility(VISIBLE);
                    }
                }
            });
        }
    }

    private boolean hasAtMention() {
        if (stalkEditText.getText().toString().substring(0, 1).equals("@")) {
            return true;
        } else {
            return false;
        }
    }

    private void searchForTag() {
        final String stringStripped = stalkEditText.getText().toString().substring(1);
        if (stringStripped.length() >= 20) {
            noUsersTextView.setText("No tags 2");
            showNotValid();
        }

        if (stringStripped.length() == 0) {
            noUsersTextView.setText("No tags");
            showNotValid();
        } else if (!searchTxtIsValid()) {
            noUsersTextView.setText("No tags");
            showNotValid();
        } else {
            Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
            boolean hasSpecialChar = p.matcher(stringStripped).find();

            if (hasSpecialChar) {
                noUsersTextView.setText("No tags");
                showNotValid();
            } else {
                //Fetch stream here
                StreamModel.fetch(stringStripped, new StreamModel.StreamCallback() {
                    @Override
                    public void callbackCall(StreamModel streamModel) {
                        tagStreamModel = streamModel;
                        stalkersCountText.setVisibility(VISIBLE);
                        stalkLoadingView.stopAnimation();
                        uiForStalking(DataHelper.hasTag(stringStripped));
                        stalkBtn.setVisibility(VISIBLE);
                        stalkersCountText.setText(UtilHelper.stalkersStringForNumber(tagStreamModel.getStalkers_count()));
                    }
                }, new SimpleCallback() {
                    @Override
                    public void callbackCall(ErrorModel errorModel) {
                        if (errorModel != null) {
                            tagStreamModel = null;
                            stalkersCountText.setVisibility(VISIBLE);
                            stalkLoadingView.stopAnimation();
                            uiForStalking(DataHelper.hasTag(stringStripped));
                            stalkBtn.setVisibility(VISIBLE);
                            stalkersCountText.setText(UtilHelper.stalkersStringForNumber(0));
                        }
                    }
                });
            }
        }
    }

    private void uiForStalking(boolean isStalking) {
        String stalkText = isStalking ? "STALKING" : "STALK";
        UIHelper.layoutBtn(getContext(), stalkBtn, stalkText);
        stalkBtn.setTextColor(getResources().getColor(isStalking ? R.color.cyan : R.color.white));
    }

    private boolean searchTxtIsValid() {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(stalkEditText.getText().toString());
        boolean found = matcher.find();

        if (found) {
            return false;
        }
        return true;
    }

    public void showNotValid() {
        stalkLoadingView.stopAnimation();
        noUsersTextView.setTextColor(getContext().getResources().getColor(R.color.gray));
        noUsersTextView.setVisibility(VISIBLE);
    }


}
