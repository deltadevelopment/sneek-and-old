package no.twomonkeys.sneek.app.shared;

import android.content.Context;

/**
 * Created by simenlie on 10.05.16.
 */
public class UIHelper {

    static public int dpToPx(Context c, int dp) {
        final float scale = c.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

}
