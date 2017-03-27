package applock.mindorks.com.applock.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import applock.mindorks.com.applock.Activity.MyAct;
import applock.mindorks.com.applock.R;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class SettingsFrag extends Fragment {

    LinearLayout llThemes, llCustomizeLock, llPassSettings, llEnableLock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings_frag, container, false);
        llThemes = (LinearLayout) rootView.findViewById(R.id.ll_themes);
        llCustomizeLock = (LinearLayout) rootView.findViewById(R.id.ll_customize_lock);
        llPassSettings = (LinearLayout) rootView.findViewById(R.id.ll_pass_setting);
        llEnableLock = (LinearLayout) rootView.findViewById(R.id.ll_enable_lock);


        llThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAct.viewPager.setCurrentItem(2);
                MyAct.tab = MyAct.tabLayout.getTabAt(2);
                MyAct.tab.select();
            }
        });
        return rootView;
    }
}
