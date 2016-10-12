package net.melove.app.chat.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by lzan13 on 2015/6/3.
 */
public class MLViewPagerAdapter extends FragmentPagerAdapter {

    private String mTabsTitle[];
    private Fragment mFragments[];

    public MLViewPagerAdapter(FragmentManager fm, Fragment fragments[], String args[]) {
        super(fm);
        mFragments = fragments;
        mTabsTitle = args;
    }




    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mTabsTitle.length;
    }
}
