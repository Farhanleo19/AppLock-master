package applock.mindorks.com.applock.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.PasswordSetActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;

public class SettingsAct extends AppCompatActivity {

    ImageView ivLockType;
    TextView tvLockType;
    Button bOk;
    Button bDone;
    RadioGroup rg;
    RadioButton rbPattern, rbPin, rbAlpha;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    int selectedId = 0;
    LinearLayout llChangePass;
    LinearLayout llLockType;
    Context context;
    String lockType = "";
    private Dialog dialog;
    public static boolean changePass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ivLockType = (ImageView) findViewById(R.id.iv_locktype);
        tvLockType = (TextView) findViewById(R.id.tv_lock_type);
        llChangePass = (LinearLayout) findViewById(R.id.ll_changepass);
        llLockType = (LinearLayout) findViewById(R.id.ll_locktype);
        pref = getSharedPreferences("lock_type", MODE_PRIVATE);
        bDone = (Button) findViewById(R.id.b_done);

        ed = pref.edit();
        lockType = pref.getString("lock_type", "");
        selectedId = pref.getInt("selected_id", 0);


        tvLockType.setText(lockType);

        if (lockType.contains("Pattern")) {
            ivLockType.setImageResource(R.drawable.pattern_icon);
        } else if (lockType.contains("Pin")) {
            ivLockType.setImageResource(R.drawable.pin_iocn);
        } else if (lockType.contains("Alphabet")) {
            ivLockType.setImageResource(R.drawable.passward_icon);
        }
        if (lockType.equals("")) {
            tvLockType.setText("Pattern Lock");
            lockType = "Pattern";
        }
        context = SettingsAct.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        llLockType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLockType();

            }
        });
        llChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass = true;
                if (lockType.contains("Pattern")) {
                    startActivity(new Intent(SettingsAct.this, PasswordSetActivity.class));

                } else if (lockType.contains("Pin")) {
                    showPinLockDialog();
                } else if (lockType.contains("Alphabet")) {
                    showAlphabetLockDialog();
                }

            }
        });

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockType.contains("Pattern")) {
                    ed.putString("lock_type", lockType);
                    ed.commit();
                    finish();
                }
                if (lockType.contains("Pin")) {
                    String pin = pref.getString("pin", "");
                    if (pin.length() > 0) {
                        ed.putString("lock_type", lockType);
                        ed.commit();
                        finish();
                    } else {
                        Toast.makeText(context, "Please set Pin first", Toast.LENGTH_SHORT).show();
                    }

                } else if (lockType.contains("Alphabet")) {
                    String pass = pref.getString("pass", "");
                    if (pass.length() > 0) {
                        ed.putString("lock_type", lockType);
                        ed.commit();
                        finish();
                    } else {
                        Toast.makeText(context, "Please set Password first", Toast.LENGTH_SHORT).show();
                    }
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

                ed.putInt("selected_id", selectedId);
                ed.commit();
                tvLockType.setText(lockType);


                if (lockType.contains("Pattern")) {
                    ivLockType.setImageResource(R.drawable.pattern_icon);
                } else if (lockType.contains("Pin")) {
                    ivLockType.setImageResource(R.drawable.pin_iocn);
                } else if (lockType.contains("Alphabet")) {
                    ivLockType.setImageResource(R.drawable.passward_icon);
                }
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
        View view = layoutInflater.inflate(R.layout.pin_lock, null);
        final EditText edPin = (EditText) view.findViewById(R.id.ed_pin);
        TextView tvHeader = (TextView) view.findViewById(R.id.tv_header);
        tvHeader.setText("ENTER NEW PIN");


        ImageView ivKey1, ivKey2, ivKey3, ivKey4, ivKey5, ivKey6, ivKey7, ivKey8, ivKey9, ivKey0, ivKeyDel, ivKeyEnter;
        ivKey0 = (ImageView) view.findViewById(R.id.key_0);
        ivKey1 = (ImageView) view.findViewById(R.id.key_1);
        ivKey2 = (ImageView) view.findViewById(R.id.key_2);
        ivKey3 = (ImageView) view.findViewById(R.id.key_3);
        ivKey4 = (ImageView) view.findViewById(R.id.key_4);
        ivKey5 = (ImageView) view.findViewById(R.id.key_5);
        ivKey6 = (ImageView) view.findViewById(R.id.key_6);
        ivKey7 = (ImageView) view.findViewById(R.id.key_7);
        ivKey8 = (ImageView) view.findViewById(R.id.key_8);
        ivKey9 = (ImageView) view.findViewById(R.id.key_9);
        ivKeyDel = (ImageView) view.findViewById(R.id.key_del);
        ivKeyEnter = (ImageView) view.findViewById(R.id.key_enter);
        ivKey0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("0");
            }
        });
        ivKey1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("1");
            }
        });
        ivKey2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("2");
            }
        });
        ivKey3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("3");
            }
        });
        ivKey4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("4");
            }
        });
        ivKey5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("5");
            }
        });
        ivKey6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("6");
            }
        });
        ivKey7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("7");
            }
        });
        ivKey8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("8");
            }
        });
        ivKey9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPin.append("9");
            }
        });

        ivKeyEnter.setVisibility(View.VISIBLE);
        ivKeyDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DEL

                int length = edPin.getText().length();
                if (length > 0) {
                    edPin.getText().delete(length - 1, length);
                }
            }
        });
        ivKeyEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ENTER

                String newPin = edPin.getText().toString();
                if (newPin.length() == 4) {
                    ed.putString("pin", newPin);
                    ed.commit();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Please enter 4 digit pin", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        edPin.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() == 4) {
//                    String pinPref = pref.getString("pin", "");
//                    String pin = edPin.getText().toString();
//                    if (pin.equals(pinPref)) {
////                    startActivity(new Intent(context,));
//                        dialog.dismiss();
//                        AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
//                    } else {
//
//                        Toast.makeText(context, "WRONG PIN", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(view);
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edPass, InputMethodManager.SHOW_IMPLICIT);
        edPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    String newPass = edPass.getText().toString();
                    if (newPass.length() > 3) {
                        ed.putString("pass", newPass);
                        ed.commit();
                        dialog.dismiss();
                    } else {
                        edPass.setError("Min 4 Characters");
                    }

                }
                return false;
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
//            case R.id.action_lock:
//                Toast.makeText(this, "Lock", Toast.LENGTH_SHORT)
//                        .show();
//                break;
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

    @Override
    public void onBackPressed() {
        if (lockType.contains("Pattern")) {
            super.onBackPressed();
        }
        if (lockType.contains("Pin")) {
            String pin = pref.getString("pin", "");
            if (pin.length() > 0) {
                super.onBackPressed();
            } else {
                Toast.makeText(context, "Please set Pin first", Toast.LENGTH_SHORT).show();
            }

        } else if (lockType.contains("Alphabet")) {
            String pass = pref.getString("pass", "");
            if (pass.length() > 0) {
                super.onBackPressed();
            } else {
                Toast.makeText(context, "Please set Password first", Toast.LENGTH_SHORT).show();
            }
        } else {

        }

    }
}
