package no.twomonkeys.sneek.app.shared.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;

/**
 * Created by simenlie on 14.05.16.
 */
public class LoadingFragment extends Fragment {

    ImageView imageView;
    ImageView bg;
    boolean shouldAnimate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.loadingFragment);

        bg = (ImageView) view.findViewById(R.id.loadingImageBg);
        bg.setImageResource(R.drawable.splash2);
        imageView = (ImageView) view.findViewById(R.id.loadingImage);
        imageView.setImageResource(R.drawable.triangle);
        imageView.setColorFilter(Color.parseColor("#ffffff"));
        bg.setScaleType(ImageView.ScaleType.CENTER);


        view.setBackgroundColor(Color.BLUE);


        return view;
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
        FragmentManager fragmentManager = getChildFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(this);
        ft.commit();
    }

}
