package ahmed.adel.sleeem.clowyy.triptracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import ahmed.adel.sleeem.clowyy.triptracker.AddNotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.MainActivity2;
import ahmed.adel.sleeem.clowyy.triptracker.NotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.R;

public class BubbleService extends Service {

    private BubblesManager bubblesManager;
    private NotificationBadge mBadge;
    BubbleLayout bubbleView;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton showBtn,addBtn,closeBtn;

    private int MY_PERMISSION = 1000;

    String tripID="TvmvuTNlezfG0RxywgzG5osEQxl1";

    public BubbleService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//        tripID = intent.getStringExtra("TripId");

//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
//        nMgr.cancel(1124);
        initBubble();



//        Intent intent1 = new Intent(getApplicationContext(), MyService.class);
//        stopService(intent1);


        return super.onStartCommand(intent, flags, startId);
    }

    private void initBubble() {
        bubblesManager = new BubblesManager.Builder(this)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        addNewBubble();
                    }
                }).build();
        bubblesManager.initialize();
    }

    private void addNewBubble() {
        bubbleView = (BubbleLayout) LayoutInflater.from(getApplicationContext())
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
                Intent notes= new Intent(getApplicationContext(), NotesDialog.class);
                notes.putExtra("tripID", tripID);
                notes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(notes);
                floatingActionsMenu.collapse();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addnotes= new Intent(getApplicationContext(), AddNotesDialog.class);
                addnotes.putExtra("tripID", tripID);
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
    public void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }
    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}