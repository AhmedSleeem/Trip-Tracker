package ahmed.adel.sleeem.clowyy.triptracker.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ahmed.adel.sleeem.clowyy.triptracker.MainActivity;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyService;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyWorker;
import ahmed.adel.sleeem.clowyy.triptracker.service.SnoozeWorker;

public class DialogAlert extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intent1 = new Intent(context, MyService.class);
        context.stopService(intent1);

        String destination = intent.getStringExtra("destination");
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("Title");

        Data inputData = new Data.Builder()
                .putString("destination", destination)
                .putString("source", destination)
                .putString("title", destination)
                .putString("Date", date).build();

//        Calendar calendarmsd = Calendar.getInstance();
//        long nowMillis = calendarmsd.getTimeInMillis();
//        long diff = nowMillis+ 2*60*1000;

        int duration = 2;
        String timeStr = " min";

        Toast.makeText(context, "Snoozed for " + duration + timeStr, Toast.LENGTH_LONG).show();

        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(SnoozeWorker.class)
                        .setInputData(inputData)
                        .setInitialDelay(duration, TimeUnit.MINUTES)
                        .build();
        WorkManager.getInstance(context).enqueue(uploadWorkRequest);



//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }


//        RemoteViews collapsedView = new RemoteViews(context.getPackageName(),
//                R.layout.notification_collapsed);
//        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
//                R.layout.activity_dialog);
//        Intent clickIntent = new Intent(context, MainActivity.class);
//        PendingIntent clickPendingIntent = PendingIntent.getActivity(context,
//                1, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        //collapsedView.setTextViewText(R.id.text_view_collapsed_1, "Hello World!");
//
//        expandedView.setOnClickPendingIntent(R.id.start, clickPendingIntent);
//
//
//        NotificationCompat.Action answerAction = new NotificationCompat.Action.Builder(R.drawable.ic_timer, "Answer", clickPendingIntent).build();
//        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(R.drawable.ic_date, "Cancel", clickPendingIntent).build();
//
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "channel_id");
//        notification.setLargeIcon((BitmapFactory.decodeResource(context.getResources(), R.drawable.trip)))
//                .setContentTitle("Hello Alarm")
//                .setSmallIcon(R.drawable.trip)
//                .setContentIntent(clickPendingIntent)
//                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_CALL)
//                .addAction(answerAction)
//                .addAction(cancelAction)
//                .setColor(Color.BLUE)
//                .setOngoing(true)
//                .setFullScreenIntent(clickPendingIntent, true).setCustomHeadsUpContentView(expandedView);
//
//
//        Log.i("huiy", UUID.nameUUIDFromBytes("ahmedpng".getBytes()).toString());
//        Log.i("huiy", UUID.nameUUIDFromBytes("ahmedpng".getBytes()).toString());
//        ;

        // notification.setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        //notification.setCustomHeadsUpContentView(expandedView);

       // notificationManager.notify(123, notification.build());


//
//        context.startActivity(intentD);


    }

}
