package applock.mindorks.com.applock.Activity;

import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import applock.mindorks.com.applock.Adapter.ApplicationListAdapter;
import applock.mindorks.com.applock.Adapter.LockedApplicationListAdapter;
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Data.AppInfo;
import applock.mindorks.com.applock.Fragments.AppLockFrag;
import applock.mindorks.com.applock.MainActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;
import applock.mindorks.com.applock.Utils.FakeLockedModel;
import applock.mindorks.com.applock.Utils.SharedPreference;

public class AllAppsAct extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static Button bDone;
    SharedPreference sharedPreference;
    Context context;
    SwitchCompat switchAll;
    public static boolean selectAll = false, UnSelectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        bDone = (Button) findViewById(R.id.b_done);
        switchAll = (SwitchCompat) findViewById(R.id.switch_all);
        context = AllAppsAct.this;

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ApplicationListAdapter(MainActivity.getListOfInstalledApp(this), this, AppLockConstants.ALL_APPS);
        mRecyclerView.setAdapter(mAdapter);


        sharedPreference = new SharedPreference();
//        ArrayList<FakeLockedModel> fakeLockedApps = sharedPreference.getFakeLocked(AllAppsAct.this);
//        if (fakeLockedApps!= null) {
//            for (int i = 0; i < fakeLockedApps.size(); i++) {
//                String pkg = fakeLockedApps.get(i).getPkgName();
//                boolean fake = fakeLockedApps.get(i).isFake();
//                Log.i("fake_status", pkg + " = " + fake + " size = " + fakeLockedApps.size());
//            }
//        }
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ApplicationListAdapter.checkedList != null && ApplicationListAdapter.checkedList.size() > 0) {
//                    for (int i = 0; i < ApplicationListAdapter.checkedList.size(); i++) {
//                        String app = ApplicationListAdapter.checkedList.get(i);
//                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", app);
//                        sharedPreference.addLocked(context, app);
//                    }
//                    ApplicationListAdapter.checkedList.clear();
//                }
//                if (ApplicationListAdapter.unCheckedList != null && ApplicationListAdapter.unCheckedList.size() > 0) {
//                    for (int j = 0; j < ApplicationListAdapter.unCheckedList.size(); j++) {
//                        String app = ApplicationListAdapter.unCheckedList.get(j);
//                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", app);
//                        sharedPreference.removeLocked(context, app);
//                    }
//                    ApplicationListAdapter.unCheckedList.clear();
//                }
                AppLockFrag.mAdapter = new LockedApplicationListAdapter((MainActivity.getListOfInstalledApp(context)), context, AppLockConstants.LOCKED);
                AppLockFrag.mRecyclerView.setAdapter(AppLockFrag.mAdapter);
                AppLockFrag.mAdapter.notifyDataSetChanged();
                finish();

            }
        });

        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   selectAllPackages();


                } else {

                    unSelectAllPkg();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        AppLockFrag.mAdapter = new LockedApplicationListAdapter((MainActivity.getListOfInstalledApp(context)), context, AppLockConstants.LOCKED);
        AppLockFrag.mRecyclerView.setAdapter(AppLockFrag.mAdapter);
        AppLockFrag.mAdapter.notifyDataSetChanged();
    }



    public void selectAllPackages() {
        List<String> apps = new ArrayList<>();
       // if (sharedPreference.getLocked(context) != null)
         //   apps = sharedPreference.getLocked(context);

        for (int i = 0; i < ApplicationListAdapter.installedApps.size(); i++) {
            if(!isPackgeAdded(ApplicationListAdapter.installedApps.get(i).getPackageName())) {
                //apps.add(ApplicationListAdapter.installedApps.get(i).getPackageName());
                sharedPreference.addLocked(context, ApplicationListAdapter.installedApps.get(i).getPackageName());
            }


            if(i==ApplicationListAdapter.installedApps.size()-1)
            {
                mAdapter.notifyDataSetChanged();
            }
        }

    }
    public boolean isPackgeAdded(String appName) {
        List<String> apps = new ArrayList<>();
        if (sharedPreference.getLocked(context) != null)
         apps = sharedPreference.getLocked(context);
        for (int i = 0; i < apps.size(); i++) {
            if (appName.equals(apps.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void unSelectAllPkg()
    {
        List<String> apps = new ArrayList<>();
        if (sharedPreference.getLocked(context) != null)
            apps = sharedPreference.getLocked(context);


        for(int i=0;i<apps.size();i++) {
            if (isPackgeAdded(apps.get(i)))
                sharedPreference.removeLocked(context, apps.get(i));


            if(i==apps.size()-1)
            {
                mAdapter.notifyDataSetChanged();
            }
        }

    }
}
