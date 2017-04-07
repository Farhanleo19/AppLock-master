package applock.mindorks.com.applock.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.services.AppCheckServices;

public class DialogActFake extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor ed;
    boolean applock;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fake);
        context = DialogActFake.this;

        Button bOK = (Button) findViewById(R.id.b_ok);


        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }
}
