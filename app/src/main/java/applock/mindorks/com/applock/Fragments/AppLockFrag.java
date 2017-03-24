package applock.mindorks.com.applock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import applock.mindorks.com.applock.AllAppsAct;
import applock.mindorks.com.applock.R;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class AppLockFrag extends Fragment {

    Button bPlus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.app_lock_frag, container, false);

        bPlus = (Button) rootView.findViewById(R.id.b_plus);

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllAppsAct.class));
            }
        });
        return rootView;
    }
}
