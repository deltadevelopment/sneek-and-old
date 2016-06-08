package no.twomonkeys.sneek.app.shared.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.components.story.StoryFragment;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 17.05.16.
 */
public class MoreView extends RelativeLayout {

    Button stalkButton;
    Button reportButton;
    Button deleteAllButton;
    Button deleteButton;
    Button stalkStreamBtn;
    Button blockBtn;
    TextView streamTitle;
    TextView usernameTitle;
    float startX;
    boolean isYourself;
    TextView stalkersTxt;
    FrameLayout bg;
    LinearLayout userSection;
    LinearLayout streamSection;
    StoryFragment.MoreViewModel viewModel;
    public SimpleCallback2 onStalkUser, onStalkStream, onBlock, onReport, onDelete, onDeleteAll, onHidden;
    Button backBtn;

    public MoreView(Context context) {
        super(context);
        initializeViews(context);
    }

    public MoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.more_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.


        stalkButton = (Button) findViewById(R.id.stalkBtn);
        // stalkButton.setText("STALK");

        reportButton = (Button) findViewById(R.id.reportBtn);
        reportButton.setText("REPORT / BLOCK");

        deleteAllButton = (Button) findViewById(R.id.deleteAllBtn);
        deleteAllButton.setText("DELETE ALL");
        deleteButton = (Button) findViewById(R.id.deleteBtn);
        deleteButton.setText("DELETE");

        blockBtn = (Button) findViewById(R.id.blockBtn);
        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });

        stalkStreamBtn = (Button) findViewById(R.id.stalkStreamBtn);
        streamTitle = (TextView) findViewById(R.id.streamTitle);
        usernameTitle = (TextView) findViewById(R.id.usernameTitle);
        stalkersTxt = (TextView) findViewById(R.id.stalkersTxt);

        deleteAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        blockBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBlock.callbackCall();
            }
        });
        stalkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.isStalkingUser) {
                    layoutBtn(stalkButton, "STALK");
                    stalkButton.setTextColor(getResources().getColor(R.color.white));
                } else {
                    layoutBtn(stalkButton, "STALKING");
                    stalkButton.setTextColor(getResources().getColor(R.color.cyan));
                }
                onStalkUser.callbackCall();
            }
        });
        stalkStreamBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onStalkStream.callbackCall();
            }
        });

        //stalkButton.setVisibility(INVISIBLE);
        //reportButton.setVisibility(INVISIBLE);

        deleteAllButton.setY(100);

        int screenHeight = UIHelper.screenHeight(getContext()) / 2;
        int screenWidth = UIHelper.screenWidth(getContext());
        //deleteButton.setY(screenHeight + (screenHeight / 2));
        //  deleteAllButton.setY(screenHeight + (screenHeight / 6));
        //  startX = deleteButton.getX();
        //deleteButton.setX(screenWidth);
        // deleteAllButton.setX(screenWidth);
        // applyUIToButton(deleteAllButton);
        //applyUIToButton(deleteButton);

        bg = (FrameLayout) findViewById(R.id.moreBg);
        bg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                animateOut();
                return true;
            }
        });
        bg.setVisibility(INVISIBLE);
        layoutBtn(stalkStreamBtn, "STALK");
        layoutBtn(stalkButton, "STALK");
        layoutBtn(blockBtn, "BLOCK");
        layoutBtn(reportButton, "REPORT");
        layoutBtn(deleteButton, "DELETE MOMENT");
        layoutBtn(deleteAllButton, "DELETE ALL");
        layoutBtnRelative(backBtn, "BACK");
        Log.v("INFLATING", "INFLATING");
    }

    public void layoutBtnRelative(Button btn, String title) {
        btn.setBackgroundColor(getResources().getColor(R.color.black));
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));

        btn.setText(title);
        int margin = UIHelper.dpToPx(getContext(), 5);
        int btnHeight = UIHelper.dpToPx(getContext(), 30);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.height = btnHeight;

        params.setMargins(0, margin * 2, 0, 0);

        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(btn.getTypeface());// your preference here
        paint.setTextSize(btn.getTextSize());// have this the same as your text size

        String text = btn.getText().toString();

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height = bounds.height();
        text_width = bounds.width() + (margin * 2) + 10;
        params.width = text_width;
        btn.setLayoutParams(params);
    }

    public void layoutBtn(Button btn, String title) {
        btn.setBackgroundColor(getResources().getColor(R.color.black));
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));

        btn.setText(title);
        int margin = UIHelper.dpToPx(getContext(), 5);
        int btnHeight = UIHelper.dpToPx(getContext(), 30);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.height = btnHeight;
        params.gravity = Gravity.CENTER;

        params.setMargins(margin, 0, margin, 0);

        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(btn.getTypeface());// your preference here
        paint.setTextSize(btn.getTextSize());// have this the same as your text size

        String text = btn.getText().toString();

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height = bounds.height();
        text_width = bounds.width() + (margin * 2) + 10;
        params.width = text_width;
        btn.setLayoutParams(params);
    }

    public void applyUIToButton(Button btn) {
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));
        btn.setTextSize(18);
    }

    public void updateView() {
        if (viewModel.streamTitle != null) {
            streamTitle.setText("#" + viewModel.streamTitle.toUpperCase());
            stalkersTxt.setText(viewModel.stalkersCountTxt + " STALKERS");
            if (viewModel.isStalkingStream) {
                layoutBtn(stalkStreamBtn, "STALKING");
                stalkStreamBtn.setTextColor(getResources().getColor(R.color.cyan));
            } else {
                layoutBtn(stalkStreamBtn, "STALK");
                stalkStreamBtn.setTextColor(getResources().getColor(R.color.white));
            }
        }
        usernameTitle.setText(viewModel.username.toUpperCase());
        if (viewModel.isStalkingUser) {
            Log.v("IS TALKING", "JA STAlKER BRUKER");
            layoutBtn(stalkButton, "STALKING");
            stalkButton.setTextColor(getResources().getColor(R.color.cyan));
        } else {
            layoutBtn(stalkButton, "STALK");
            stalkButton.setTextColor(getResources().getColor(R.color.white));
        }
        if (viewModel.isBlocked) {
            layoutBtn(blockBtn, "BLOCKED");
            blockBtn.setTextColor(getResources().getColor(R.color.gray));
        } else {
            layoutBtn(blockBtn, "BLOCK");
            blockBtn.setTextColor(getResources().getColor(R.color.white));
        }

    }

    public void setup(int mode) {
        // 0 = user
        // 1 = user_device
        // 2 = stream with device user
        // 3 = stream without device user
        switch (mode) {
            case 0: {
                deleteAllButton.setVisibility(GONE);
                deleteButton.setVisibility(GONE);
                streamTitle.setVisibility(GONE);
                stalkStreamBtn.setVisibility(GONE);
                stalkersTxt.setVisibility(GONE);

                stalkButton.setVisibility(VISIBLE);
                reportButton.setVisibility(VISIBLE);
                blockBtn.setVisibility(VISIBLE);
                usernameTitle.setVisibility(VISIBLE);
                break;
            }
            case 1: {
                deleteAllButton.setVisibility(VISIBLE);
                deleteButton.setVisibility(VISIBLE);
                usernameTitle.setVisibility(VISIBLE);

                streamTitle.setVisibility(GONE);
                stalkStreamBtn.setVisibility(GONE);
                stalkersTxt.setVisibility(GONE);


                stalkButton.setVisibility(GONE);
                reportButton.setVisibility(GONE);
                blockBtn.setVisibility(GONE);

                break;
            }
            case 2: {
                deleteAllButton.setVisibility(GONE);
                deleteButton.setVisibility(VISIBLE);
                usernameTitle.setVisibility(VISIBLE);

                streamTitle.setVisibility(VISIBLE);
                stalkStreamBtn.setVisibility(VISIBLE);
                stalkersTxt.setVisibility(VISIBLE);


                stalkButton.setVisibility(GONE);
                reportButton.setVisibility(GONE);
                blockBtn.setVisibility(GONE);

                break;
            }
            case 3: {
                deleteAllButton.setVisibility(GONE);
                deleteButton.setVisibility(GONE);
                usernameTitle.setVisibility(VISIBLE);

                streamTitle.setVisibility(VISIBLE);
                stalkStreamBtn.setVisibility(VISIBLE);
                stalkersTxt.setVisibility(VISIBLE);


                stalkButton.setVisibility(VISIBLE);
                reportButton.setVisibility(VISIBLE);
                blockBtn.setVisibility(VISIBLE);
                break;
            }
        }


    }

    public void animateIn() {
        bg.setVisibility(VISIBLE);
        bg.animate().alpha(0.7f).setDuration(150);
        deleteAllButton.animate().translationX(startX).setDuration(150);


        //deleteButton.animate().translationX(startX).setDuration(150);
    }

    public void animateOut() {
        //bg.animate().alpha(0.0f).setDuration(150);
        int screenWidth = UIHelper.screenWidth(getContext());
        deleteAllButton.animate().translationX(screenWidth).setDuration(150);
        //deleteButton.animate().translationX(screenWidth).setDuration(150);

        ObjectAnimator anim = ObjectAnimator.ofFloat(bg, "alpha", 0.0f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bg.setVisibility(INVISIBLE);
                setAlpha(0.0f);
                onHidden.callbackCall();
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

    public void setViewModel(StoryFragment.MoreViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
