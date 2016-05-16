package no.twomonkeys.sneek.app.shared.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import no.twomonkeys.sneek.app.shared.helpers.UIHelper;

/**
 * Created by simenlie on 16.05.16.
 */
public class ProgressIndicator extends View {

    public boolean isFilling;

    public ProgressIndicator(Context context, boolean isFilling) {
        super(context);
        this.isFilling = isFilling;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);

        paint.setStrokeWidth(4);
        paint.setColor(Color.WHITE);
        if (isFilling) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }

        paint.setAntiAlias(true);

        /*
        * UIBezierPath *path = [UIBezierPath new];
    [path moveToPoint:(CGPoint){0, height}];
    [path addLineToPoint:(CGPoint){(width/2), 0}];
    [path addLineToPoint:(CGPoint){width, height}];
    [path closePath];
        * */


        int width = UIHelper.dpToPx(getContext(), 35);
        int height = UIHelper.dpToPx(getContext(), 35) - UIHelper.dpToPx(getContext(), 5);

        Point a = new Point(0, height);
        Point b = new Point(width / 2, 0);
        Point c = new Point(width, height);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        /*
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.moveTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        */

        int starter = 10;

        path.moveTo(starter, height - starter);
        path.lineTo((width / 2), starter);
        path.lineTo(width -starter, height - starter);

        path.close();


        canvas.drawPath(path, paint);

        //canvas.drawPath(path, paint);

    }
}
