package applock.mindorks.com.applock.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import applock.mindorks.com.applock.Fragments.AppLockFrag;
import applock.mindorks.com.applock.Fragments.ThemeFrag;
import applock.mindorks.com.applock.Fragments.SettingsFrag;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public TabsPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                SettingsFrag tab1 = new SettingsFrag();
                return tab1;
            case 1:
                AppLockFrag tab2 = new AppLockFrag();
                return tab2;
            case 2:
                ThemeFrag tab3 = new ThemeFrag();
                return tab3;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
