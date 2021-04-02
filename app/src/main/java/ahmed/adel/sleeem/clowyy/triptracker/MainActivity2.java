package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.model.LatLng;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class MainActivity2 extends AppCompatActivity {

    private BubblesManager bubblesManager;
    private NotificationBadge mBadge;
    BubbleLayout bubbleView;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton showBtn,addBtn,closeBtn;

    private int MY_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMap(getIntent(), getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity2.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        } else {
            Intent intent = new Intent(MainActivity2.this, Service.class);
            startService(intent);
        }

        initBubble();

        addNewBubble();
        finish();
    }

    private void initBubble() {
        bubblesManager = new BubblesManager.Builder(this)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                    }
                }).build();
        bubblesManager.initialize();
    }

    private void addNewBubble() {
        bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity2.this)
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
                Intent notes= new Intent(MainActivity2.this,NotesDialog.class);
                notes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(notes);
                floatingActionsMenu.collapse();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addnotes= new Intent(MainActivity2.this,AddNotesDialog.class);
                addnotes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addnotes);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }

    private void startMap(Intent intent, Context context){
        String destination = intent.getStringExtra("destination");
        String tripId = intent.getStringExtra("TripId");

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

    }
}