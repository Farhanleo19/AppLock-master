package applock.mindorks.com.applock.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import applock.mindorks.com.applock.Adapter.GridViewAdapter;
import applock.mindorks.com.applock.R;

/**
 * Created by farhanbutt19@gmail.com on 3/24/2017.
 */
public class ThemeFrag extends Fragment {

    Context context;
    ArrayList<Integer> imagesArray = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_frag, container, false);
        context = this.getActivity();

        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        imagesArray.add(R.drawable.wall);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_view);
        gridview.setAdapter(new GridViewAdapter(context, imagesArray));
        return rootView;
    }
}
