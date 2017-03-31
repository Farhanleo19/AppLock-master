package applock.mindorks.com.applock.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import applock.mindorks.com.applock.AppLockConstants;
import applock.mindorks.com.applock.Data.AppInfo;
import applock.mindorks.com.applock.Fragments.AppLockFrag;
import applock.mindorks.com.applock.MainActivity;
import applock.mindorks.com.applock.R;
import applock.mindorks.com.applock.Utils.FakeLockedModel;
import applock.mindorks.com.applock.Utils.SharedPreference;

/**
 * Created by Farhan on 28/04/15.
 */
public class LockedApplicationListAdapter extends RecyclerView.Adapter<LockedApplicationListAdapter.ViewHolder> {
    List<AppInfo> lockedApps = new ArrayList();
    private Context context;
    SharedPreference sharedPreference;
    String requiredAppsType;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;
    boolean fake_lock = false;
    ArrayList<String> fakeLockedList = new ArrayList<String>();

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView applicationName;
//        public CardView cardView;
        public ImageView icon, iv_fake_lock;

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
//            cardView = (CardView) v.findViewById(R.id.card_view);
            icon = (ImageView) v.findViewById(R.id.icon);
            iv_fake_lock = (ImageView) v.findViewById(R.id.iv_fake_lock);
            prefs = context.getSharedPreferences(
                    "lock_type", Context.MODE_PRIVATE);
            ed = prefs.edit();


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
    public LockedApplicationListAdapter(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
        lockedApps = appInfoList;
        this.context = context;
        this.requiredAppsType = requiredAppsType;
        sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        boolean flag = true;
        if (requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            for (int i = 0; i < lockedApps.size(); i++) {
                flag = true;
                if (sharedPreference.getLocked(context) != null) {
                    for (int j = 0; j < sharedPreference.getLocked(context).size(); j++) {
                        if (lockedApps.get(i).getPackageName().matches(sharedPreference.getLocked(context).get(j))) {
                            lockedFilteredAppList.add(lockedApps.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(lockedApps.get(i));
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                lockedApps.clear();
                lockedApps.addAll(lockedFilteredAppList);
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
                lockedApps.clear();
                lockedApps.addAll(unlockedFilteredAppList);
            }


        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LockedApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.locked_item_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = lockedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

//        holder.cardView.setOnClickListener(null);
        holder.iv_fake_lock.setTag(position);

        holder.iv_fake_lock.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       int pos = (Integer) v.getTag();
                                                       boolean remove = false;
                                                       Log.i("name", appInfo.getPackageName());
                                                       String app = appInfo.getPackageName();
                                                       fakeLockedList = sharedPreference.getFakeLocked(context);

                                                       if (fakeLockedList != null && fakeLockedList.size() > 0) {
                                                           for (int i = 0; i < fakeLockedList.size(); i++) {
                                                               sharedPreference.addFakeLocked(context, app);
                                                               if (app.matches(fakeLockedList.get(i))) {

                                                                   sharedPreference.removeFakeLocked(context, app);
                                                                   fakeLockedList.remove(pos);

                                                               }
                                                           }

                                                       } else {
                                                           sharedPreference.addFakeLocked(context, app);
                                                       }
//


                                                       if (fakeLockedList != null && fakeLockedList.size() > 0) {
                                                           Log.i("fake_locked_list", fakeLockedList.toString());
                                                           for (int i = 0; i < fakeLockedList.size(); i++) {
//                                                               if (appInfo.getPackageName().matches(fakeLockedList.get(i))) {
//                                                                   sharedPreference.removeFakeLocked(context, app);
//                                                                   fakeLockedList.remove(i);
//                                                               }else{
//
//                                                               }

                                                           }

                                                       } else {
//                                                               sharedPreference.addFakeLocked(context, app);
                                                       }


//                fakeLockedList = sharedPreference.getFakeLocked(context);

//                // Add FakeLocked
//                FakeLockedModel fakeLockedModel = new FakeLockedModel(appInfo.getPackageName(), fake_lock);
//                sharedPreference.removeFakeLocked(context, fakeLockedModel);
//                // Remove Fake Locked
//                FakeLockedModel fakeLockedModel = new FakeLockedModel(appInfo.getPackageName(), fake_lock);
//                sharedPreference.addFakeLocked(context, fakeLockedModel);
                                                   }
                                               }

        );


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lockedApps.size();
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

}