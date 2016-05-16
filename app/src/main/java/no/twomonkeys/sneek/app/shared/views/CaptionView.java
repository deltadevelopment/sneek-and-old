package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 16.05.16.
 */
public class CaptionView extends RelativeLayout {
    TextView textViewOne;
    TextView textViewTwo;

    public CaptionView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CaptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CaptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.caption_view, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.

        textViewOne = (TextView) findViewById(R.id.textViewOne);
        textViewTwo = (TextView) findViewById(R.id.textViewTwo);
        textViewOne.setTextSize(15);
        textViewTwo.setTextSize(15);

        textViewOne.setBackgroundColor(getContext().getResources().getColor(R.color.black));
        textViewOne.setTextColor(getContext().getResources().getColor(R.color.white));
        textViewTwo.setBackgroundColor(getContext().getResources().getColor(R.color.black));
        textViewTwo.setTextColor(getContext().getResources().getColor(R.color.white));

        textViewTwo.setVisibility(INVISIBLE);
    }

    public void updateCaption(int numberOfLines, int secondLineStart, String caption) {

        if (numberOfLines > 1) {
            String lineOne = caption.substring(0, secondLineStart);
            String lineTwo = caption.substring(secondLineStart, caption.length());
            layoutTextViews(textViewOne, lineOne, 0);
            layoutTextViews(textViewTwo, lineTwo, 100);
            textViewTwo.setVisibility(VISIBLE);
        } else {
            layoutTextViews(textViewOne, caption, 0);
        }

    }

    private void layoutTextViews(TextView txtView, String captionPart, float y) {
        txtView.setText(captionPart);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Rect bounds = UIHelper.sizeForView(txtView, captionPart);
        int margin = UIHelper.dpToPx(getContext(), 10);
        params.height = UIHelper.dpToPx(getContext(), 30);
        params.width = bounds.width() + margin + 10;
        txtView.setPadding(margin / 2, margin / 2, margin / 2, 0);

        params.setMargins(margin, margin + (int) y, margin + UIHelper.dpToPx(getContext(), 50), 0);

        txtView.setLayoutParams(params);
    }

}
