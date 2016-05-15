package no.twomonkeys.sneek.app.components.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import no.twomonkeys.sneek.R;
import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.UIHelper;
import no.twomonkeys.sneek.app.shared.models.FeedModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 09.05.16.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private LayoutInflater inflater;
    FeedModel feedModel;
    private View view;

    public FeedAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        feedModel = new FeedModel(context);
    }

    public void updateData(final SimpleCallback scb) {
        feedModel.fetch(new SimpleCallback() {
            @Override
            public void callbackCall() {
                notifyDataSetChanged();
                Log.v("ADAPTER", " size " + feedModel.getStories().size());
                scb.callbackCall();
            }
        });
    }

    public FeedModel getFeedModel() {
        return feedModel;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = inflater.inflate(R.layout.custom_row, parent, false);
        int height = parent.getMeasuredHeight() / 2;
        int width = parent.getMeasuredWidth() / 2;

        //view.setMinimumHeight(height);
        //view.setMinimumWidth(width);
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.rowRoot);
        gridLayout.setMinimumWidth(width);
        gridLayout.setMinimumHeight(height);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        view.setLayoutParams(layoutParams);

        RelativeLayout lay = (RelativeLayout) view.findViewById(R.id.contentGrid);

        //
        FeedViewHolder holder = new FeedViewHolder(view, width, height);

        return holder;
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        Log.v("TEST", "ROW : " + position);
        StoryModel storyModel = feedModel.getStories().get(position);

        Point cellSize = DataHelper.currentFeed() == 0 ? storyModel.getCellSize() : storyModel.getBigCellSize();

        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.rowRoot);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.width = cellSize.x;
        layoutParams.height = cellSize.y;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        view.setLayoutParams(layoutParams);


        holder.updateTxt(storyModel);
    }

    @Override
    public int getItemCount() {
        return feedModel.getStories().size();
    }

}
