package neu.droid.guy.watchify.FirstRunScreens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * For Showing Fragments
 */
public class FragmentsAdapter extends FragmentPagerAdapter {

    private static final int NUMBER_OF_FRAGMENTS = 3;

    FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ScreenOne.newInstance();
            case 1:
                return ScreenTwo.newInstance();
            case 2:
                return ScreenThree.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }
}