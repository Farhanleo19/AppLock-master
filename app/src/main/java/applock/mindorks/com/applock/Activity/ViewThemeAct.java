package applock.mindorks.com.applock.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import applock.mindorks.com.applock.R;

public class ViewThemeAct extends AppCompatActivity {

    ImageView ivThemeView;
    TextView tvThemeNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_theme);

        ivThemeView = (ImageView) findViewById(R.id.iv_theme_view);
        tvThemeNumber = (TextView) findViewById(R.id.tv_theme_num);

    }
}
