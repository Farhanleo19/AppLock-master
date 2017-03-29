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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.takwolf.android.lock9.Lock9View;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import applock.mindorks.com.applock.Activity.MyAct;
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
    public static String currentApp = "";
    public static String previousApp = "";
    SharedPreference sharedPreference;
    List<String> pakageName;
    AlertDialog alertDialog;
    boolean fake_lock = false;
    boolean pin_lock = false;
    boolean alpha_lock = false;
    boolean pattern_lock = false;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;
    String lockType = "";

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
                            if (!currentApp.matches(previousApp)) {
                                fake_lock = prefs.getBoolean("fake_lock", false);
                                // Getting Saved Preferences
                                lockType = prefs.getString("lock_type", "");
                                Log.i("type_select", lockType);
                                if (lockType.contains("Pin")) {
                                    // Pin
                                    showPinLockDialog();
                                } else if (fake_lock) {
                                    // Fake
                                    showFakeLockDialog();
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
        View promptsView = layoutInflater.inflate(R.layout.pin_lock, null);
        TextView tvHeader = (TextView) promptsView.findViewById(R.id.tv_header);
        tvHeader.setText("PLEASE ENTER YOUR PIN");
        final EditText edPin = (EditText) promptsView.findViewById(R.id.ed_pin);
        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);

        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    void showAlphabetLockDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.alphabet_lock, null);
        TextView tvHeader = (TextView) promptsView.findViewById(R.id.tv_header);
        final EditText edPass = (EditText) promptsView.findViewById(R.id.ed_pass);
        tvHeader.setText("ENTER YOUR PASSWORD");
        Button bProceed = (Button) promptsView.findViewById(R.id.b_proceed);
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passPref = prefs.getString("pass", "");
                String pass = edPass.getText().toString();
                if (pass.equals(passPref)) {
                    dialog.dismiss();
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
                } else {
                    Toast.makeText(context, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                }
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

    void showFakeLockDialog() {
        if (context == null)
            context = getApplicationContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.fake_lock, null);
        Button bOk = (Button) promptsView.findViewById(R.id.b_ok);


        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
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
