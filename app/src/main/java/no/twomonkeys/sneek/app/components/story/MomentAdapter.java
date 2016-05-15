package no.twomonkeys.sneek.app.components.story;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 13.05.16.
 */
public class MomentAdapter extends FragmentStatePagerAdapter {

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    ArrayList<MomentModel> moments;

    public MomentAdapter(FragmentManager fm) {
        super(fm);
        moments = new ArrayList<>();
    }

    public void setMoments(ArrayList<MomentModel> moments) {
        this.moments = moments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        Log.v("POs", "page ");
        return super.getItemPosition(object);
    }

    @Override
    public MomentFragment getItem(int i) {
        MomentFragment fragment = new MomentFragment();
        fragment.setMomentModel(moments.get(i));
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(MomentFragment.ARG_OBJECT, i + 1);

        Log.v("Pager", "page " + i);
        fragment.setArguments(args);

        //fragment.updateView();
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.v("Pager", "page " + position);
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return moments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
