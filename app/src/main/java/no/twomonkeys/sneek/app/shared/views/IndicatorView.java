package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 16.05.16.
 */
public class IndicatorView extends RelativeLayout {

    int maxSize;
    ProgressIndicator progressIndicatorOutline;
    ProgressIndicator progressIndicatorFill;

    public IndicatorView(Context context) {
        super(context);
        initializeViews(context);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.indicator_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
        maxSize = UIHelper.dpToPx(getContext(), 35);
        progressIndicatorOutline = new ProgressIndicator(getContext(), false);
        RelativeLayout.LayoutParams prms = new RelativeLayout.LayoutParams(maxSize, maxSize);
        progressIndicatorOutline.setLayoutParams(prms);

        progressIndicatorFill = new ProgressIndicator(getContext(), true);
        ViewGroup.LayoutParams prms2 = new ViewGroup.LayoutParams(0, maxSize);
        progressIndicatorFill.setLayoutParams(prms2);

        addView(progressIndicatorOutline);
        addView(progressIndicatorFill);
    }


    public void updateProgress(float percent) {

        float size = maxSize * percent;
        float endResult = (size / 100);
        int endResult2 = (int) endResult;
        Log.v("PErcent", "percent is " + percent + " max " + endResult2 + " " + maxSize);

        RelativeLayout.LayoutParams prms2 = new RelativeLayout.LayoutParams(endResult2,maxSize);
        progressIndicatorFill.setLayoutParams(prms2);
    }

}
