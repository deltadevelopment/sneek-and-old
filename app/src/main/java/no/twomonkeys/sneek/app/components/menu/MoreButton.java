package no.twomonkeys.sneek.app.components.menu;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 10.05.16.
 */
public class MoreButton extends Button {

    private float dX;
    private  int width;

    public MoreButton(Context context) {
        super(context);

        //setText("RANDOMS");
        setBackgroundColor(getResources().getColor(R.color.black));
        setTextColor(getResources().getColor(R.color.white));
        setTextSize(17);

        setTypeface(Typeface.create("HelveticaNeue", 0));

    }

    public void updateTxt(String txt) {
        setText(txt);
        int margin = UIHelper.dpToPx(getContext(), 10);
        int btnHeight = UIHelper.dpToPx(getContext(), 50);

        setPadding(margin, 0, margin, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = btnHeight;
        params.setMargins(0, margin, 0, 0);

        //rl.addView(button);
        Log.v("WIDTH: ", "" + params.width);
        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(getTypeface());// your preference here
        paint.setTextSize(getTextSize());// have this the same as your text size

        String text = txt;

        paint.getTextBounds(text, 0, text.length(), bounds);

        text_height = bounds.height();
        text_width = bounds.width() + (margin * 2) + 10;
        params.width = text_width;
        setLayoutParams(params);
        width = text_width;
        setX(-width);
    }

    public void enableBtn() {
        setBackgroundColor(getResources().getColor(R.color.black));
        setTextColor(getResources().getColor(R.color.white));
    }

    public void disableBtn() {
        setBackgroundColor(getResources().getColor(R.color.white));
        setTextColor(getResources().getColor(R.color.black));
    }

    public void startMove(float x) {
        dX = getX() - x;
    }

    public void moveRight(float x) {
        float result = x + dX;

        if (result < 0){
            setX(result);
        }
        else{
            setX(0);
        }
    }

    public float percentageScrolled()
    {
        float min = getX();
        float maxPos = -width;
        float result = (min/maxPos);
        return result;
    }

    public void animateOut()
    {
        animate().translationX(-width).setDuration(50);
    }
    public void animateIn()
    {
        animate().translationX(0).setDuration(50);
    }

}
