package ahmed.adel.sleeem.clowyy.triptracker.managers;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.model.LatLng;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import ahmed.adel.sleeem.clowyy.triptracker.AddNotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.MainActivity2;
import ahmed.adel.sleeem.clowyy.triptracker.NotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class DoneTripReceiver extends BroadcastReceiver {

    private BubblesManager bubblesManager;
    private NotificationBadge mBadge;
    BubbleLayout bubbleView;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton showBtn,addBtn,closeBtn;

    private int MY_PERMISSION = 1000;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String destination = intent.getStringExtra("destination");
        String tripId = intent.getStringExtra("TripId");
        int notificationId = intent.getIntExtra("notificationId",0);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(notificationId);

        Log.i("Done receiver", "onReceive: "+destination);


        TripDao tripDao = TripDatabase.getInstance(context).getTripDao();

        Trip trip = tripDao.selectTripById(tripId);

        trip.setTripStatus(true);

        tripDao.updateTrip(trip);


        LatLng location = GoogleMapsManager.getInstance(context).getLocationFromAddress(destination);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(mapIntent);

//        context.startActivity(new Intent(context, MainActivity2.class));

        Intent intent1;

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            ((AppCompatActivity)context).startActivityForResult(intent1, 0);
        } else {
            intent1 = new Intent(context, Service.class);
            context.startService(intent1);
        }
        */


       // initBubble();
        //addNewBubble();
    }

    private void initBubble() {
        bubblesManager = new BubblesManager.Builder(context)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                    }
                }).build();
        bubblesManager.initialize();
    }

    private void addNewBubble() {
        bubbleView = (BubbleLayout) LayoutInflater.from(context)
                .inflate(R.layout.bubble_layout,null);

        mBadge = (NotificationBadge)bubbleView.findViewById(R.id.count);
        mBadge.setNumber(1);

        floatingActionsMenu = bubbleView.findViewById(R.id.fab);
        showBtn = bubbleView.findViewById(R.id.showNotes);
        addBtn = bubbleView.findViewById(R.id.addNotes);
        closeBtn = bubbleView.findViewById(R.id.closeNotes);

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notes= new Intent(context, NotesDialog.class);
                notes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(notes);
                floatingActionsMenu.collapse();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addnotes= new Intent(context, AddNotesDialog.class);
                addnotes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(addnotes);
                floatingActionsMenu.collapse();
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubblesManager.removeBubble(bubbleView);
            }
        });

        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

}