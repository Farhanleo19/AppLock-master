package applock.mindorks.com.applock.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import applock.mindorks.com.applock.R;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class ThemeFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_frag, container, false);

        return rootView;
    }
}
