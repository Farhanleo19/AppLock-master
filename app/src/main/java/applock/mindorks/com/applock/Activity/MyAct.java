package applock.mindorks.com.applock.Activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.takwolf.android.lock9.Lock9View;

import applock.mindorks.com.applock.Adapter.TabsPagerAdapter;
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Custom.FlatButton;
import applock.mindorks.com.applock.PasswordRecoveryActivity;
import applock.mindorks.com.applock.PasswordSetActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;
import applock.mindorks.com.applock.Utils.SharedPreference;

public class MyAct extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    Notification notification;
    //This is our tablayout
    public static TabLayout tabLayout;
    public static TabLayout.Tab tab;
    //This is our viewPager
    public static ViewPager viewPager;
    Context context;
    private Dialog dialog;
    SharedPreference sharedPreference;
    SharedPreferences pref;
    SharedPreferences.Editor ed;
    boolean isFirstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_act);
        context = MyAct.this;

        showNotification();
        sharedPreference = new SharedPreference();
        pref = getSharedPreferences("lock_type", MODE_PRIVATE);
        ed = pref.edit();
        isFirstRun = isFirstRun();
        if (isFirstRun) {
            ed.putBoolean("run_status", false);
            ed.commit();
            startActivity(new Intent(context, PasswordSetActivity.class));
        }
        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("SETTINGS"));
        tabLayout.addTab(tabLayout.newTab().setText("APP LOCK"));
        tabLayout.addTab(tabLayout.newTab().setText("THEME"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tab = tabLayout.getTabAt(1);
        tab.select();

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                tab.select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }


    public void showNotification() {

        Intent intent = new Intent(context, DialogAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        intent.putExtra("detailID", pos);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentText("App lock is running");
        mBuilder.setTicker(context.getResources().getString(R.string.app_name));
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.ic_launcher));
        mBuilder.build();
        notification = mBuilder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
//        if (sharedPref.getTajweed_Notification() == true) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, notification);

//        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

    public boolean isFirstRun() {

        isFirstRun = pref.getBoolean("run_status", true);
        return isFirstRun;
    }
}
