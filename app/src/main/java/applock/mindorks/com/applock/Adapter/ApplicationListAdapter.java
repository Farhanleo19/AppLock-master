package applock.mindorks.com.applock.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import applock.mindorks.com.applock.Activity.AllAppsAct;
import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Data.AppInfo;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;
import applock.mindorks.com.applock.Utils.SharedPreference;

/**
 * Created by Farhan on 28/04/15.
 */
public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {
    public static List<AppInfo> installedApps = new ArrayList();
    private Context context;
    SharedPreference sharedPreference;
    String requiredAppsType;
    List<String> apps = new ArrayList<>();
//    public static ArrayList<String> checkedList = new ArrayList<String>();
//    public static ArrayList<String> unCheckedList = new ArrayList<String>();

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView applicationName;
        //        public CardView cardView;
        public ImageView icon;
        public SwitchCompat switchView;

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
//            cardView = (CardView) v.findViewById(R.id.card_view);
            icon = (ImageView) v.findViewById(R.id.icon);
            switchView = (SwitchCompat) v.findViewById(R.id.switchView);
        }
    }

    public void add(int position, String item) {
//        mDataset.add(position, item);
//        notifyItemInserted(position);
    }

    public void remove(AppInfo item) {
//        int position = installedApps.indexOf(item);
//        installedApps.remove(position);
//        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ApplicationListAdapter(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
        installedApps = appInfoList;
        this.context = context;
        this.requiredAppsType = requiredAppsType;
        sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        boolean flag = true;
        if (requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            for (int i = 0; i < installedApps.size(); i++) {
                flag = true;
                if (sharedPreference.getLocked(context) != null) {
                    for (int j = 0; j < sharedPreference.getLocked(context).size(); j++) {
                        if (installedApps.get(i).getPackageName().matches(sharedPreference.getLocked(context).get(j))) {
                            lockedFilteredAppList.add(installedApps.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(installedApps.get(i));
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                installedApps.clear();
                installedApps.addAll(lockedFilteredAppList);
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
                installedApps.clear();
                installedApps.addAll(unlockedFilteredAppList);
            }
        }


        if (sharedPreference.getLocked(context) != null)
            apps = sharedPreference.getLocked(context);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = installedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

//        if (AllAppsAct.selectAll) {
//            holder.switchView.setChecked(true);
//        } else {
//            holder.switchView.setChecked(false);
//        }


        holder.switchView.setOnCheckedChangeListener(null);
//        holder.cardView.setOnClickListener(null);

        if (checkLockedItem(appInfo.getPackageName())) {
            holder.switchView.setChecked(true);
        } else {
            holder.switchView.setChecked(false);
        }



        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", appInfo.getPackageName());

                    if (!isPackgeAdded(appInfo.getPackageName()))
                        sharedPreference.addLocked(context, appInfo.getPackageName());


                } else {


                    AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", appInfo.getPackageName());

                    if (isPackgeAdded(appInfo.getPackageName()))
                        sharedPreference.removeLocked(context, appInfo.getPackageName());
                }
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    /*Checks whether a particular app exists in SharedPreferences*/
    public boolean checkLockedItem(String checkApp) {
        boolean check = false;
        List<String> locked = sharedPreference.getLocked(context);
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    public boolean isPackgeAdded(String appName) {
        for (int i = 0; i < apps.size(); i++) {
            if (appName.equals(apps.get(i))) {
                return true;
            }
        }
        return false;
    }
}