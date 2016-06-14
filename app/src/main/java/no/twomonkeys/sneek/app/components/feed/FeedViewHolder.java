package no.twomonkeys.sneek.app.components.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import org.w3c.dom.Text;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.SimpleCallback2;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;
import no.twomonkeys.sneek.app.shared.views.LoadingFragment;
import no.twomonkeys.sneek.app.shared.views.LoadingView;

/**
 * Created by simenlie on 12.05.16.
 */
public class FeedViewHolder extends RecyclerView.ViewHolder {

    TextView usernameTxtView;
    ImageView storyImageView;
    SimpleDraweeView draweeView;
    LoadingView loadingView;
    Context c;

    public FeedViewHolder(View itemView, int width, int height) {
        super(itemView);
        c = itemView.getContext();

        storyImageView = new ImageView(c);
        storyImageView.setImageResource(R.drawable.splash2);
        storyImageView.setBackgroundColor(Color.parseColor("#939393"));

        loadingView = (LoadingView) itemView.findViewById(R.id.feedLoadingView);

        draweeView = (SimpleDraweeView) itemView.findViewById(R.id.feedImg);
        //draweeView = new SimpleDraweeView(c);
        GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
        hierarchy.setFadeDuration(0);
        hierarchy.setPlaceholderImage(R.drawable.splash2);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = height;
        params.width = width;
        params.setMargins(0, 10, 0, 0);
        draweeView.setLayoutParams(params);
        loadingView.setLayoutParams(params);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params2.width = width;
        params2.height = UIHelper.dpToPx(c, 30);
        usernameTxtView =  (TextView) itemView.findViewById(R.id.feedUsername);
        usernameTxtView.setBackgroundColor(c.getResources().getColor(R.color.white));
        usernameTxtView.setLayoutParams(params2);
        usernameTxtView.setTextColor(c.getResources().getColor(R.color.black));
    }

    public void updateTxt(StoryModel storyModel, int feedCount) {
        String txt;
        if (storyModel.getStream_type() != null)
        {
            txt = "#" + storyModel.getName().toUpperCase();
        }
        else{
            txt = storyModel.getUserModel().getUsername().toUpperCase();
        }

        Rect storyFrame = feedCount > 2 ? storyModel.getFrame() : storyModel.getBigFrame();

        //storyImageView.setX(storyFrame.left);
        // storyImageView.setY(storyFrame.top);

        //x, y, width, height
        //left, top, right, bottom

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(200, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.width = storyFrame.right;
        params2.height = storyFrame.bottom;
        params2.setMargins(storyFrame.left, storyFrame.top, 0, 0);
        Log.v("WIDTH", "width" + storyFrame.width());
        draweeView.setLayoutParams(params2);
        loadingView.setLayoutParams(params2);
        loadingView.startAnimate();
        MomentModel momentModel = storyModel.getCurrentMoment();

        momentModel.loadPhoto(draweeView, new SimpleCallback2() {
            @Override
            public void callbackCall() {
                loadingView.stopAnimation();
            }
        });

        Rect bounds = UIHelper.sizeForView(usernameTxtView, txt);
        int margin = UIHelper.dpToPx(c, 10);
        usernameTxtView.setText(txt);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.height = UIHelper.dpToPx(c, 30);
        params.width = bounds.width() + margin + 10;
        //left, top, right, bottom

        usernameTxtView.setPadding(margin / 2, margin / 2, margin / 2, 0);
        params.setMargins(margin + storyFrame.left, margin + storyFrame.top, 0, 0);
        usernameTxtView.setLayoutParams(params);
        //usernameTxtView.setWidth(bounds.width() + 10);

    }
}
