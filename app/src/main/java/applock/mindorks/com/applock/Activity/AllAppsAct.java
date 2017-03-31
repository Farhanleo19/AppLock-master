package applock.mindorks.com.applock.Activity;

import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import applock.mindorks.com.applock.Adapter.ApplicationListAdapter;
import applock.mindorks.com.applock.Adapter.LockedApplicationListAdapter;
import applock.mindorks.com.applock.AppLockConstants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        bDone = (Button) findViewById(R.id.b_done);
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
                if (ApplicationListAdapter.checkedList != null && ApplicationListAdapter.checkedList.size() > 0) {
                    for (int i = 0; i < ApplicationListAdapter.checkedList.size(); i++) {
                        String app = ApplicationListAdapter.checkedList.get(i);
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", app);
                        sharedPreference.addLocked(context, app);
                    }
                    ApplicationListAdapter.checkedList.clear();
                }
                if (ApplicationListAdapter.unCheckedList != null && ApplicationListAdapter.unCheckedList.size() > 0) {
                    for (int j = 0; j < ApplicationListAdapter.unCheckedList.size(); j++) {
                        String app = ApplicationListAdapter.unCheckedList.get(j);
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", app);
                        sharedPreference.removeLocked(context, app);
                    }
                    ApplicationListAdapter.unCheckedList.clear();
                }
                AppLockFrag.mAdapter = new LockedApplicationListAdapter((MainActivity.getListOfInstalledApp(context)), context, AppLockConstants.LOCKED);
                AppLockFrag.mRecyclerView.setAdapter(AppLockFrag.mAdapter);
                AppLockFrag.mAdapter.notifyDataSetChanged();
                finish();

            }
        });
    }


}
