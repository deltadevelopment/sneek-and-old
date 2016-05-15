package no.twomonkeys.sneek.app.shared.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;

/**
 * Created by simenlie on 15.05.16.
 */
public class LoadingView extends RelativeLayout {

    ImageView imageView;
    ImageView bg;
    boolean shouldAnimate;

    public LoadingView(Context context) {
        super(context);
        initializeViews(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.loading_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        bg = (ImageView) findViewById(R.id.loadingImageBg);
        bg.setImageResource(R.drawable.splash2);
        imageView = (ImageView) findViewById(R.id.loadingImage);
        imageView.setImageResource(R.drawable.triangle);
        imageView.setColorFilter(Color.parseColor("#ffffff"));
        bg.setScaleType(ImageView.ScaleType.CENTER);


        this.setBackgroundColor(Color.BLUE);
    }

    public void startAnimate() {
        shouldAnimate = true;
        animateToAlpha(0.5f);
    }

    public void animateToAlpha(final float alpha) {
        if (shouldAnimate) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(imageView, "alpha", alpha);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Do something.
                    if (shouldAnimate) {
                        animateToAlpha(alpha == 0.5f ? 0.0f : 0.5f);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.setDuration(300).start();
        }
    }

    public void stopAnimation() {
        shouldAnimate = false;
        setVisibility(INVISIBLE);
    }

}
