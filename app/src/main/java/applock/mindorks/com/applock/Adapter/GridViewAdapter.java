package applock.mindorks.com.applock.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import applock.mindorks.com.applock.Activity.ViewThemeAct;
import applock.mindorks.com.applock.R;

/**
 * Created by farhanbutt19@gmail.com on 3/27/2017.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Integer> imagesArray = new ArrayList<Integer>();

    public GridViewAdapter(Context context, ArrayList<Integer> imagesArray) {
        this.context = context;
        this.imagesArray = imagesArray;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.theme_item, null);

            // set value into textview


            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.iv_theme);
            imageView.setImageResource(imagesArray.get(position));
            Button bView = (Button) gridView.findViewById(R.id.b_view);
            Button bApply = (Button) gridView.findViewById(R.id.b_apply);

            bView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ViewThemeAct.class));
                }
            });
            bApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Apply", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return imagesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
