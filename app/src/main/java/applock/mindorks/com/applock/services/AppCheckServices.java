package applock.mindorks.com.applock.services;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.takwolf.android.lock9.Lock9View;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import applock.mindorks.com.applock.Activity.DialogAct;
import applock.mindorks.com.applock.Activity.DialogActFake;
import applock.mindorks.com.applock.Activity.FakeAct;
import applock.mindorks.com.applock.Activity.MyAct;
import applock.mindorks.com.applock.Adapter.LockedApplicationListAdapter;
import applock.mindorks.com.applock.AppLockApplication;
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Custom.FlatButton;
import applock.mindorks.com.applock.PasswordRecoveryActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;
import applock.mindorks.com.applock.Utils.SharedPreference;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class AppCheckServices extends Service {

    public static final String TAG = "AppCheckServices";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;
    private Dialog dialog;
    SharedPreference sharedPreference;
    public static String currentApp = "";
    public static String previousApp = "";

    List<String> pakageName;
    AlertDialog alertDialog;
    boolean fake_lock = false;
    boolean pin_lock = false;
    boolean alpha_lock = false;
    boolean pattern_lock = false;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;
    String lockType = "";
    public static ArrayList<String> fakeLockedList = new ArrayList<String>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreference = new SharedPreference();
        if (sharedPreference != null) {
            pakageName = sharedPreference.getLocked(context);
        }
        prefs = context.getSharedPreferences("lock_type", Context.MODE_PRIVATE);
        ed = prefs.edit();
        timer = new Timer("AppCheckServices");
        timer.schedule(updateTask, 1000L, 1000L);

        final Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.APP_LOCK);
        t.send(new HitBuilders.AppViewBuilder().build());

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, params);


    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (sharedPreference != null) {
                pakageName = sharedPreference.getLocked(context);
            }
            if (isConcernedAppIsInForeground()) {
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            fake_lock = false;
                            fakeLockedList = sharedPreference.getFakeLocked(context);
                            if (fakeLockedList != null && fakeLockedList.size() > 0) {
                                for (int i = 0; i < fakeLockedList.size(); i++) {
                                    String fakeApp = "";
                                    fakeApp = fakeLockedList.get(i);
                                    if (currentApp.matches(fakeApp)) {
                                        fake_lock = true;
                                        break;
                                    } else {
                                        fake_lock = false;
                                    }
                                }

//                            fake_lock = prefs.getBoolean("fake_lock", false);
                                if (!currentApp.matches(previousApp) && fake_lock) {

                                    showFakeLockDialog();
                                    previousApp = currentApp;

                                }
                            }
                            if (!currentApp.matches(previousApp) && fake_lock == false) {

                                // Getting Saved Preferences
                                lockType = prefs.getString("lock_type", "");
                                Log.i("type_select", lockType);
                                if (lockType.contains("Pin")) {
                                    // Pin
                                    showPinLockDialog();
                                } else if (fake_lock) {
                                    // Fake

                                } else if (lockType.contains("Pattern")) {
                                    // Pattern
                                    showUnlockDialog();
                                } else if (lockType.contains("Alphabet")) {
                                    // Alphabet
                                    showAlphabetLockDialog();
                                }

                                // Show Fake Lock Dialog


                                previousApp = currentApp;
                            }
                        }
                    });
                }
            } else {
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            hideUnlockDialog();
                        }
                    });
                }
            }
        }
    };

//    void showFakeLockDialog() {
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog = new AlertDialog.Builder(this)
//                .setMessage("Unfortunately, app has been stopped?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        alertDialog.dismiss();
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    }

    void showUnlockDialog() {
        showDialog();
    }

    void hideUnlockDialog() {
        previousApp = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showPinLockDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pin_lock, null);
        TextView tvHeader = (TextView) view.findViewById(R.id.tv_header);
        tvHeader.setText("TYPE YOUR SECURITY PIN");
        final EditText edPin = (EditText) view.findViewById(R.id.ed_pin);

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

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });

        edPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    String pinPref = prefs.getString("pin", "");
                    String pin = edPin.getText().toString();
                    if (pin.equals(pinPref)) {
//                    startActivity(new Intent(context,));
                        dialog.dismiss();
                        AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
                    } else {

                        Toast.makeText(context, "WRONG PIN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);

//        bProceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String pinPref = prefs.getString("pin", "");
//                String pin = edPin.getText().toString();
//                if (pin.equals(pinPref)) {
////                    startActivity(new Intent(context,));
//                    dialog.dismiss();
//                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
//                } else {
//
//                    Toast.makeText(context, "WRONG PIN", Toast.LENGTH_SHORT).show();
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
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
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
        TextView tvHeader = (TextView) promptsView.findViewById(R.id.tv_header);
        final EditText edPass = (EditText) promptsView.findViewById(R.id.ed_pass);
        tvHeader.setText("ENTER YOUR PASSWORD");
//        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);
//        bProceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });
        edPass.requestFocus();
        edPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String passPref = prefs.getString("pass", "");
                    String pass = edPass.getText().toString();
                    if (pass.equals(passPref)) {
                        dialog.dismiss();
                        AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
                    } else {
                        Toast.makeText(context, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        dialog.show();

    }

    void showFakeLockDialog() {


//        if (context == null)
//            context = getApplicationContext();

        Intent dialogAct = new Intent(this, DialogActFake.class);
        dialogAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogAct);
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View promptsView = layoutInflater.inflate(R.layout.fake_lock, null);
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
//
//            }
//        });
//
//        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        dialog.setContentView(promptsView);
//        dialog.getWindow().setGravity(Gravity.CENTER);
//
//        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode,
//                                 KeyEvent event) {
//
//                if (keyCode == KeyEvent.KEYCODE_BACK
//                        && event.getAction() == KeyEvent.ACTION_UP) {
//                    Intent startMain = new Intent(Intent.ACTION_MAIN);
//                    startMain.addCategory(Intent.CATEGORY_HOME);
//                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(startMain);
//                }
//                return true;
//            }
//        });
//
//        dialog.show();

    }

    void showDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.popup_unlock, null);
        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
        FlatButton forgetPassword = (FlatButton) promptsView.findViewById(R.id.forgetPassword);
        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.matches(sharedPreference.getPassword(context))) {
                    dialog.dismiss();
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Pattern Try Again", Toast.LENGTH_SHORT).show();
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Wrong Password", "wrong_password", "");
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppCheckServices.this, PasswordRecoveryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                dialog.dismiss();
                AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Forget Password", "forget_password", "");
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
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });

        dialog.show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

        }
        /* We want this service to continue running until it is explicitly
        * stopped, so return sticky.
        */
        return START_STICKY;
    }

    public boolean isConcernedAppIsInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        if (Build.VERSION.SDK_INT <= 20) {
            if (task.size() > 0) {
                ComponentName componentInfo = task.get(0).topActivity;
                for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                        currentApp = pakageName.get(i);

                        return true;
                    }
                }
            }
        } else {
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    Log.d(TAG, "isEmpty Yes");
                    mpackageName = "";
                } else {
                    mpackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.d(TAG, "isEmpty No : " + mpackageName);
                }
            }


            for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                if (mpackageName.equals(pakageName.get(i))) {
                    currentApp = pakageName.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if (imageView != null) {
            windowManager.removeView(imageView);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
