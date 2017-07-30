package com.android.huminskiy1325.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cubru on 26.07.2017.
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.android.huminskiy1325.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE = "com.android.huminskiy1325.photogallery.PRIVATE";

    private static final int POLL_INTERVAL = 1000 * 60 * 5; // 15 секунд

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * Used to name the worker thread, important only for debugging.
     */
    public PollService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    void showBackgroundNotification(int requestCode, Notification notification){
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra("REQUEST_CODE", requestCode);
        intent.putExtra("NOTIFICATION", notification);

        sendOrderedBroadcast(intent,PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvalible = connectivityManager.getBackgroundDataSetting() &&
                connectivityManager.getActiveNetworkInfo() != null;
        if (!isNetworkAvalible) {
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String query = sharedPreferences.getString(FlickrFetch.PREF_SEARCH_QUERY, null);
        String lastResultId = sharedPreferences.getString(FlickrFetch.PREF_LAST_RESULT_ID, null);
        ArrayList<GalleryItem> items;
        if (query != null) {
            items = new FlickrFetch().search(query);
        } else {
            items = new FlickrFetch().fetchItems(0);
        }
        if (items.size() == 0)
            return;
        String resultId = items.get(0).getId();
        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);

            Resources r = getResources();
            PendingIntent pi = PendingIntent.getActivity(this, 0,
                    new Intent(this, PhotoGalleryActivity.class), 0);

            Notification notification = new Notification.Builder(this)
                    .setTicker(r.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(r.getString(R.string.new_pictures_title))
                    .setContentText(r.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);

        } else {
            Log.i(TAG, "Got an old result: " + resultId);
        }
        sharedPreferences.edit()
                .putString(FlickrFetch.PREF_LAST_RESULT_ID, resultId)
                .commit();
    }
}
