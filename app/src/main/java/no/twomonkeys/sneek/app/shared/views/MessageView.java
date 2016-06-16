package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 16.06.16.
 */
public class MessageView extends RelativeLayout {

    TextView messageTextView;
    Button exitBtn;
    RelativeLayout r = this;
    int initialHeight;
    int mStartWidth;
    int actualHeight;

    public MessageView(Context context) {
        super(context);
        initializeViews(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.message_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        exitBtn = (Button) findViewById(R.id.exitBtn);
        setY(UIHelper.dpToPx(getContext(), 15));
    }

    public void setMessageTypeSuccess() {
        setBackgroundColor(getContext().getColor(R.color.successColor));
    }

    public void setMessage(String txt) {
        messageTextView.setText(txt);
    }

    public void hide() {
        getLayoutParams().height = 0;
    }

    public void animateIn() {
        setY(UIHelper.dpToPx(getContext(), 55));
        mStartWidth = 0;
        actualHeight = UIHelper.dpToPx(getContext(), 40);
        Ani a = new Ani();
        a.setDuration(150);
        r.startAnimation(a);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
                DataHelper.getMa().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animateOut();
                    }
                });
            }
        }, 5000);
    }

    public void animateOut() {
        mStartWidth = UIHelper.dpToPx(getContext(), 40);
        actualHeight = 0;
        Ani a = new Ani();
        a.setDuration(150);
        r.startAnimation(a);
    }

    class Ani extends Animation {

        public Ani() {

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;

            //newHeight = (int) (initialHeight * interpolatedTime);
            newHeight = mStartWidth + (int) ((initialHeight - mStartWidth) * interpolatedTime);

            r.getLayoutParams().height = newHeight;
            r.requestLayout();

            //r.setY(newY);
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            initialHeight = actualHeight;
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    ;

}
