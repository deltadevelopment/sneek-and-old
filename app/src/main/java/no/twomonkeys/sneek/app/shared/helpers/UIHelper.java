package no.twomonkeys.sneek.app.shared.helpers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;

/**
 * Created by simenlie on 10.05.16.
 */
public class UIHelper {

    static public int dpToPx(Context c, int dp) {
        final float scale = c.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    static public Rect sizeForView(TextView view, String text)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        int text_height = 0;
        int text_width = 0;

        paint.setTypeface(view.getTypeface());// your preference here
        paint.setTextSize(view.getTextSize());// have this the same as your text size
        paint.getTextBounds(text, 0, text.length(), bounds);

        return bounds;
        //text_height = bounds.height();
        //text_width = bounds.width() + (margin * 2) + 10;
    }

    static public int screenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
    static public int screenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    public static void layoutBtnRelative(Context c, Button btn, String title) {
        btn.setBackgroundColor(c.getResources().getColor(R.color.black));
        btn.setTextColor(c.getResources().getColor(R.color.white));
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));

        btn.setText(title);
        int margin = UIHelper.dpToPx(c, 5);
        int btnHeight = UIHelper.dpToPx(c, 30);


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

    public static void layoutBtn(Context c, Button btn, String title) {
        btn.setBackgroundColor(c.getResources().getColor(R.color.black));
        btn.setTextColor(c.getResources().getColor(R.color.white));
        btn.setTypeface(Typeface.create("HelveticaNeue", 0));

        btn.setText(title);
        int margin = UIHelper.dpToPx(c, 5);
        int btnHeight = UIHelper.dpToPx(c, 30);


        ViewGroup.LayoutParams params = btn.getLayoutParams();
        params.height = btnHeight;


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

    public static void animate(ObjectAnimator objectAnimator, int duration, final SimpleCallback2 scb)
    {

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                scb.callbackCall();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(duration).start();
    }

    public static void animate(ObjectAnimator objectAnimator, int duration,int delay, final SimpleCallback2 scb)
    {

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                scb.callbackCall();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(duration).setStartDelay(delay);
        objectAnimator.start();
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

}
