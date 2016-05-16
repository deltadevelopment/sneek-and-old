package no.twomonkeys.sneek.app.shared.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

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

}
