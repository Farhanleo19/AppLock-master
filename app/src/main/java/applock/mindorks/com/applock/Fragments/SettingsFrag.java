package applock.mindorks.com.applock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import applock.mindorks.com.applock.Activity.MyAct;
import applock.mindorks.com.applock.Activity.SettingsAct;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.SharedPreference;
import applock.mindorks.com.applock.services.AppCheckServices;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class SettingsFrag extends Fragment {

    LinearLayout llThemes, llCustomizeLock, llPassSettings, llEnableLock;
    SharedPreference sharedPreference;
    Context context;
    TextView tvEnable;
    ArrayList<String> locked_apps_array = new ArrayList<String>();
    boolean applock = false;
    SharedPreferences pref;
    SharedPreferences.Editor ed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings_frag, container, false);
        llThemes = (LinearLayout) rootView.findViewById(R.id.ll_themes);
        llCustomizeLock = (LinearLayout) rootView.findViewById(R.id.ll_customize_lock);
        llPassSettings = (LinearLayout) rootView.findViewById(R.id.ll_pass_setting);
        llEnableLock = (LinearLayout) rootView.findViewById(R.id.ll_enable_lock);
        tvEnable = (TextView) rootView.findViewById(R.id.tv_enable);
        sharedPreference = new SharedPreference();
        context = this.getActivity();
        pref = context.getSharedPreferences("applock",
                Context.MODE_PRIVATE);
        ed = pref.edit();
        applock = pref.getBoolean("applock", false);
        if (applock) {
            tvEnable.setText("Enable Applock");
        } else {
            tvEnable.setText("Disable Applock");
        }
        llEnableLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (applock) {
                    applock = false;
                    ed.putBoolean("applock", applock);
                    ed.commit();

                    context.startService(new Intent(context, AppCheckServices.class));
                    tvEnable.setText("Disable Applock");
                    Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    applock = true;
                    ed.putBoolean("applock", applock);
                    ed.commit();
                    context.stopService(new Intent(context, AppCheckServices.class));
                    tvEnable.setText("Enable Applock");
                    Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
                }


            }
        });
        llThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAct.viewPager.setCurrentItem(2);
                MyAct.tab = MyAct.tabLayout.getTabAt(2);
                MyAct.tab.select();
            }
        });
        llPassSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SettingsAct.class));
            }
        });
        return rootView;
    }
}
