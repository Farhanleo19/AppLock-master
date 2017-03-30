package applock.mindorks.com.applock.Activity;

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
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Fragments.AppLockFrag;
import applock.mindorks.com.applock.MainActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.FakeLockedModel;
import applock.mindorks.com.applock.Utils.SharedPreference;

public class AllAppsAct extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Button bDone;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        bDone = (Button) findViewById(R.id.b_done);

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
                finish();

            }
        });
    }


}
