package applock.mindorks.com.applock.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.SharedPreference;
import applock.mindorks.com.applock.services.AppCheckServices;

public class DialogAct extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor ed;
    boolean applock;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_enable_service);
        context = DialogAct.this;
        pref = getSharedPreferences("applock", MODE_PRIVATE);
        applock = pref.getBoolean("applock", false);
        ed = pref.edit();

        final Button bEnable = (Button) findViewById(R.id.b_enable);
        Button bMain = (Button) findViewById(R.id.b_main);
        if (applock) {

            bEnable.setText("Enable AppLock");

        } else {
            bEnable.setText("Disable AppLock");
        }
        // if button is clicked, close the custom dialog
        bEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applock) {
                    applock = false;
                    ed.putBoolean("applock", applock);
                    ed.commit();

                    context.startService(new Intent(context, AppCheckServices.class));
                    bEnable.setText("Disable Applock");
                    Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    applock = true;
                    ed.putBoolean("applock", applock);
                    ed.commit();
                    context.stopService(new Intent(context, AppCheckServices.class));
                    bEnable.setText("Enable Applock");
                    Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        bMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MyAct.class));
                finish();
            }
        });
    }

}
