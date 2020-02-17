package edu.csc4360.thescotchdatabase;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPager extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    public FragmentPager(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Your current main fragment showing how to send arguments to fragment
                ScotchSearchFragment scotchSearchFragment = new ScotchSearchFragment();
                //Bundle data = new Bundle();
                //data.putInt("current_page", position+1);
                //scotchSearchFragment.setArguments(data);
                return scotchSearchFragment;
            case 1:
                // Calling a Fragment without sending arguments

                return new ScotchRangeSeekBarFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }

}