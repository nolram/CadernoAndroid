package com.lab11.nolram.components;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.cadernocamera.TutorialActivityFragment;

/**
 * Created by nolram on 22/10/15.
 */
public class TutorialAdapter extends FragmentPagerAdapter {

    public static final int NUM_PAGES = 6;
    private Context mContext;

    public TutorialAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_peter_river), position);
            case 1:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_amethyst), position);
            case 2:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_sun_flower), position);
            case 3:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_alizarin), position);
            case 4:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_emerald), position);
            default:
                return TutorialActivityFragment.newInstance(mContext.getResources().getColor(
                        R.color.flatui_pumpkin), position);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
