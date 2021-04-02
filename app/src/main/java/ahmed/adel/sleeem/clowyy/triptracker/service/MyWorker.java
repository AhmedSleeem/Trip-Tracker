package ahmed.adel.sleeem.clowyy.triptracker.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {
    private Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        // Log.i("TAGG", "doWork: hello y ");
        String title = getInputData().getString("Title");
        String source = getInputData().getString("Source");
        String destination = getInputData().getString("Destination");
        String tripId = getInputData().getString("Date");

        Intent intent1 = new Intent(context,MyService.class);
        intent1.putExtra("Title", title);
        intent1.putExtra("Source", source);
        intent1.putExtra("Destination", destination);
        intent1.putExtra("Date", tripId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        }else
            context.startService(intent1);

        return Result.success();
    }
}