package applock.mindorks.com.applock.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import applock.mindorks.com.applock.R;

public class SettingsAct extends AppCompatActivity {

    ImageView ivLockType;
    TextView tvLockType;
    Button bOk;
    RadioGroup rg;
    RadioButton rbPattern,rbPin,rbAlpha;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    int selectedId = 0;

    String lockType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ivLockType = (ImageView) findViewById(R.id.iv_locktype);
        tvLockType = (TextView) findViewById(R.id.tv_lock_type);
        pref = getSharedPreferences("lock_type", MODE_PRIVATE);
        ed = pref.edit();
        lockType = pref.getString("lock_type", "");
        selectedId = pref.getInt("selected_id",0);
        tvLockType.setText(lockType);

        ivLockType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLockType();


            }
        });
    }

    void selectLockType() {
        // custom dialog
        final Dialog dialog = new Dialog(SettingsAct.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_lock_type);
        dialog.setTitle("Lock Type");


        rg = (RadioGroup) dialog.findViewById(R.id.rg_types);
         rbPattern = (RadioButton) dialog.findViewById(R.id.rb_pattern);
         rbPin = (RadioButton) dialog.findViewById(R.id.rb_pin);
         rbAlpha = (RadioButton) dialog.findViewById(R.id.rb_alpha);

        if(lockType.contains("Pattern")){
            rbPattern.setChecked(true);
        }else if(lockType.contains("Pin")){
            rbPin.setChecked(true);
        }else if(lockType.contains("Alphabet")){
            rbAlpha.setChecked(true);
        }

        bOk = (Button) dialog.findViewById(R.id.b_ok);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 selectedId = rg.getCheckedRadioButtonId();

                RadioButton rb = (RadioButton) dialog.findViewById(selectedId);
                lockType = rb.getText().toString();
                ed.putString("lock_type", lockType);
                ed.putInt("selected_id", selectedId);
                ed.commit();
                tvLockType.setText(lockType);


                dialog.dismiss();

            }
        });

        // if button is clicked, close the custom dialog


        dialog.show();

    }
}
