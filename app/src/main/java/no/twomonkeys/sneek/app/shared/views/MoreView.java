package no.twomonkeys.sneek.app.shared.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 17.05.16.
 */
public class MoreView extends RelativeLayout {

    Button stalkButton;
    Button reportButton;
    Button deleteAllButton;
    Button deleteButton;
    float startX;
    boolean isYourself;
    FrameLayout bg;

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
        stalkButton.setText("STALK");

        reportButton = (Button) findViewById(R.id.reportBtn);
        reportButton.setText("REPORT / BLOCK");

        deleteAllButton = (Button) findViewById(R.id.deleteAllBtn);
        deleteAllButton.setText("DELETE ALL");
        deleteButton = (Button) findViewById(R.id.deleteBtn);
        deleteButton.setText("DELETE");


        stalkButton.setVisibility(INVISIBLE);
        reportButton.setVisibility(INVISIBLE);

        deleteAllButton.setY(100);

        int screenHeight = UIHelper.screenHeight(getContext()) / 2;
        int screenWidth = UIHelper.screenWidth(getContext());
        deleteButton.setY(screenHeight + (screenHeight / 2));
        deleteAllButton.setY(screenHeight + (screenHeight / 6));
        startX = deleteButton.getX();
        deleteButton.setX(screenWidth);
        deleteAllButton.setX(screenWidth);
        applyUIToButton(deleteAllButton);
        applyUIToButton(deleteButton);

        bg = (FrameLayout) findViewById(R.id.moreBg);
        bg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                animateOut();
                return true;
            }
        });
        bg.setVisibility(INVISIBLE);
    }

    public void applyUIToButton(Button btn) {
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));
        btn.setTextSize(18);
    }

    public void animateIn() {
        bg.setVisibility(VISIBLE);
        bg.animate().alpha(0.5f).setDuration(150);
        deleteAllButton.animate().translationX(startX).setDuration(150);
        deleteButton.animate().translationX(startX).setDuration(150);
    }

    public void animateOut() {
        //bg.animate().alpha(0.0f).setDuration(150);
        int screenWidth = UIHelper.screenWidth(getContext());
        deleteAllButton.animate().translationX(screenWidth).setDuration(150);
        deleteButton.animate().translationX(screenWidth).setDuration(150);

        ObjectAnimator anim = ObjectAnimator.ofFloat(bg, "alpha", 0.0f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                bg.setVisibility(INVISIBLE);
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
}
