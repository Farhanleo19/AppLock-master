package applock.mindorks.com.applock.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import applock.mindorks.com.applock.PasswordSetActivity;
import applock.mindorks.com.applock.R;

public class SettingsAct extends AppCompatActivity {

    ImageView ivLockType;
    TextView tvLockType;
    Button bOk;
    RadioGroup rg;
    RadioButton rbPattern, rbPin, rbAlpha;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    int selectedId = 0;
    LinearLayout llChangePass;
    Context context;
    String lockType = "";
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ivLockType = (ImageView) findViewById(R.id.iv_locktype);
        tvLockType = (TextView) findViewById(R.id.tv_lock_type);
        llChangePass = (LinearLayout) findViewById(R.id.ll_changepass);
        pref = getSharedPreferences("lock_type", MODE_PRIVATE);
        ed = pref.edit();
        lockType = pref.getString("lock_type", "");
        selectedId = pref.getInt("selected_id", 0);
        tvLockType.setText(lockType);
        if (lockType.equals("")) {
            tvLockType.setText("Pattern");
            lockType = "Pattern";
        }
        context = SettingsAct.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivLockType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLockType();

            }
        });
        llChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockType.contains("Pattern")) {
                    startActivity(new Intent(SettingsAct.this, PasswordSetActivity.class));

                } else if (lockType.contains("Pin")) {
                    showPinLockDialog();
                } else if (lockType.contains("Alphabet")) {
                    showAlphabetLockDialog();
                }

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

        if (lockType.contains("Pattern")) {
            rbPattern.setChecked(true);
        } else if (lockType.contains("Pin")) {
            rbPin.setChecked(true);
        } else if (lockType.contains("Alphabet")) {
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

    void showPinLockDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.pin_lock, null);
        final EditText edPin = (EditText) promptsView.findViewById(R.id.ed_pin);
        TextView tvHeader = (TextView) promptsView.findViewById(R.id.tv_header);
        tvHeader.setText("ENTER NEW PIN");
        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);

        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPin = edPin.getText().toString();
//                String confirmPin = "";
//                if (newPin.length() == 4) {
//                    Toast.makeText(context, "Confirm Pin", Toast.LENGTH_SHORT).show();
//                    edPin.setText("");
//                    confirmPin = edPin.getText().toString();
//                }
//                if (newPin.equals(newPin)) {
                    ed.putString("pin", newPin);
                    ed.commit();
//                } else {
//                    Toast.makeText(context, "Pin Mismatch", Toast.LENGTH_SHORT).show();
//                }
            }
        });
//        Button bOk = (Button) promptsView.findViewById(R.id.b_ok);
//
//
//        bOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
//            }
//        });

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        dialog.show();

    }

    void showAlphabetLockDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.alphabet_lock, null);
        final EditText edPass = (EditText) promptsView.findViewById(R.id.ed_pass);
        TextView tvHeader = (TextView) promptsView.findViewById(R.id.tv_header);
        tvHeader.setText("ENTER NEW PASSWORD");
        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = edPass.getText().toString();
//                String confirmPin = "";
//                if (newPin.length() == 4) {
//                    Toast.makeText(context, "Confirm Pin", Toast.LENGTH_SHORT).show();
//                    edPin.setText("");
//                    confirmPin = edPin.getText().toString();
//                }
//                if (newPin.equals(newPin)) {
                ed.putString("pass", newPass);
                ed.commit();
//                } else {
//                    Toast.makeText(context, "Pin Mismatch", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_lock:
                Toast.makeText(this, "Lock", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.action_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

}
