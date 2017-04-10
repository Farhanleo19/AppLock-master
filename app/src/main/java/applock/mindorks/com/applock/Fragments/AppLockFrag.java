package applock.mindorks.com.applock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import applock.mindorks.com.applock.Activity.AllAppsAct;
import applock.mindorks.com.applock.Activity.MyAct;
import applock.mindorks.com.applock.Adapter.ApplicationListAdapter;
import applock.mindorks.com.applock.Adapter.LockedApplicationListAdapter;
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.MainActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.SharedPreference;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class AppLockFrag extends Fragment {
    Context context;
    Button bPlus;
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SharedPreference sharedPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.app_lock_frag, container, false);
        context = this.getActivity();
        sharedPreference = new SharedPreference();
        LockedApplicationListAdapter.fakeLockedList = sharedPreference.getFakeLocked(context);
        bPlus = (Button) rootView.findViewById(R.id.b_plus);

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllAppsAct.class));
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_locked);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new LockedApplicationListAdapter((MainActivity.getListOfInstalledApp(context)), context, AppLockConstants.LOCKED);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        if (AllAppsAct.dialog != null) {
            if( AllAppsAct.dialog.isShowing()){
                AllAppsAct.dialog.dismiss();
            }

        }
    }
}
