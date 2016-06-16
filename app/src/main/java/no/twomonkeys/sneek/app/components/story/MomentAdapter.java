package no.twomonkeys.sneek.app.components.story;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.twomonkeys.sneek.app.shared.models.MomentModel;
import no.twomonkeys.sneek.app.shared.models.StoryModel;

/**
 * Created by simenlie on 13.05.16.
 */
public class MomentAdapter extends FragmentStatePagerAdapter {
    ArrayList<MomentModel> moments;
    HashMap<Integer, MomentFragment> momentPages;

    public MomentAdapter(FragmentManager fm) {
        super(fm);
        moments = new ArrayList<>();
        momentPages = new HashMap<>();
    }

    public void setMoments(ArrayList<MomentModel> moments) {
        this.moments.removeAll(this.moments);
        this.moments.addAll(moments);
        Log.v("Moments set", " moments set");
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        Log.v("POs", "page ");
        //return super.getItemPosition(object);
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public MomentFragment getItem(int i) {
        MomentFragment fragment = new MomentFragment();
        momentPages.put(i, fragment);
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
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        momentPages.remove(position);
    }

    public MomentFragment getFragment(int position)
    {
        return momentPages.get(position);
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
