package no.twomonkeys.sneek.app.components.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 12.05.16.
 */
public class FeedViewHolder extends RecyclerView.ViewHolder {

    TextView usernameTxtView;
    ImageView storyImageView;
    SimpleDraweeView draweeView;
    Context c;

    public FeedViewHolder(View itemView, int width, int height) {
        super(itemView);
        c = itemView.getContext();

        storyImageView = new ImageView(c);
        storyImageView.setImageResource(R.drawable.splash2);
        storyImageView.setBackgroundColor(Color.parseColor("#939393"));


        draweeView = new SimpleDraweeView(c);
        GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.splash2);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = height;
        params.width = width;
        params.setMargins(0, 10, 0, 0);
        draweeView.setLayoutParams(params);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params2.width = width;
        params2.height = UIHelper.dpToPx(c, 30);
        usernameTxtView = new TextView(c);
        usernameTxtView.setBackgroundColor(c.getResources().getColor(R.color.white));
        usernameTxtView.setLayoutParams(params2);
        usernameTxtView.setTextColor(c.getResources().getColor(R.color.black));
    }

    public void updateTxt(StoryModel storyModel) {
        String txt = storyModel.getUserModel().getUsername().toUpperCase();
        Rect storyFrame = DataHelper.currentFeed() == 0 ? storyModel.getFrame() : storyModel.getBigFrame();

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
        MomentModel momentModel = storyModel.getCurrentMoment();
        if (momentModel.media_type == 0)
        {
            Uri uri = Uri.parse(momentModel.getMedia_url());
            draweeView.setImageURI(uri);
        }


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