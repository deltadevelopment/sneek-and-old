package no.twomonkeys.sneek.app.components.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import no.twomonkeys.sneek.R;

/**
 * Created by simenlie on 09.05.16.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {
    private LayoutInflater inflater;

    public FeedAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_row, parent, false);
        int height = parent.getMeasuredHeight() / 2;
        int width = parent.getMeasuredWidth() /2;
        view.setMinimumHeight(height);
        view.setMinimumWidth(width);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText("Hello");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txtView);

        }
    }
}
