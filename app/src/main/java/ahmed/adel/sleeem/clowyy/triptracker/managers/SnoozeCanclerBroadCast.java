package ahmed.adel.sleeem.clowyy.triptracker.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ahmed.adel.sleeem.clowyy.triptracker.service.SnoozeService;

public class SnoozeCanclerBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, SnoozeService.class);
        context.stopService(intent1);

    }
}