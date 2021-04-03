package ahmed.adel.sleeem.clowyy.triptracker.managers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ahmed.adel.sleeem.clowyy.triptracker.service.SnoozeService;

public class SnoozeCanclerBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        int notificationId = intent.getIntExtra("notificationId",0);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(notificationId);

    }
}