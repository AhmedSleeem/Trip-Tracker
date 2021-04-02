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

public class SnoozeWorker extends Worker {
    private Context context;

    public SnoozeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        // Log.i("TAGG", "doWork: hello y ");
        String title = getInputData().getString("Title");

        String source = getInputData().getString("source");
        String destination = getInputData().getString("destination");
        String date = getInputData().getString("date");

        Intent intent1 = new Intent(context,SnoozeService.class);
        intent1.putExtra("Title", title);
        intent1.putExtra("Source", source);
        intent1.putExtra("Destination", destination);
        intent1.putExtra("Date", date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        }else
            context.startService(intent1);

        return Result.success();
    }
}