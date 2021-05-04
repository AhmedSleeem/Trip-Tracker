package ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.AddNotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.MainActivity2;
import ahmed.adel.sleeem.clowyy.triptracker.NotesDialog;
import ahmed.adel.sleeem.clowyy.triptracker.OnTripAddedNotifier;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.TripActivity;
import ahmed.adel.sleeem.clowyy.triptracker.TripDetailsActivity;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.HistoryAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.OnRecyclerViewItemClickLister;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.UpcomingTripsAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class UpcomingTripsFragment extends Fragment implements OnUpcomingAdapterItemClicked, OnTripAddedNotifier {

    private RecyclerView rv;
    private List<Trip> trips;

    TripDao tripDao;

    private BubblesManager bubblesManager;
    private static NotificationBadge mBadge;
    private static int count;
    BubbleLayout bubbleView;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton showBtn,addBtn,closeBtn;

    private int MY_PERMISSION = 1000;
    String tripID;

    Context context;


    @Override
    public void onStart() {
        super.onStart();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        trips = TripDatabase.getInstance(getContext()).getTripDao().selectAllTrips(userID,false);

        if(TripDatabase.getInstance(getContext()).getTripDao().selectAllTrips(userID).size()==0){
            getUserTrips(userID);
        }

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips,this));
        TripActivity.setOnProgressChangedListener(this);
    }


    private void getUserTrips(String userID){
        List<Trip> userTrips = new ArrayList<>();
        DatabaseReference userTripsRef = FirebaseDatabase.getInstance().getReference("trips").child(userID);
        userTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    userTrips.add(trip);
                    tripDao.insertTrip(trip);
                }

                if(userTrips != null && userTrips.size() > 0) {
                    trips = userTrips;
                    rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips,UpcomingTripsFragment.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //upcomingTripsViewModel = new ViewModelProvider(this).get(UpcomingTripsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upcomingtrips, container, false);
//        final TextView textView = root.findViewById(R.id.text_upcomingtrips);
//        upcomingTripsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        tripDao = TripDatabase.getInstance(getContext()).getTripDao();

        trips = TripDatabase.getInstance(getContext()).getTripDao().selectAllTrips();
        rv = root.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips,this));


        return root;
    }

    @Override
    public void onDetailsIconClicked(int position) {
        Intent details = new Intent(getContext(), TripDetailsActivity.class);
        details.putExtra("TripID", trips.get(position).getTripId());
        startActivity(details);
    }

    @Override
    public void onDeleteIconClicked(int position) {
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //set title
        builder.setTitle(getString(R.string.deleteMSGtitle));
        //set message
        builder.setMessage(getString(R.string.deleteMSG));
        //positive yes button
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tripID = trips.get(position).getTripId();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                trips.remove(position);
                tripDao.deleteTripId(tripID);
                FirebaseDatabase.getInstance().getReference("trips").child(userID).child(tripID).removeValue();
                WorkManager.getInstance(getContext()).cancelAllWorkByTag(tripID);
                rv.getAdapter().notifyDataSetChanged();
            }
        });
        //negative no button
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onEditIconClicked(int position) {
        Intent edit = new Intent(getContext(), TripActivity.class);
        edit.putExtra("isEdit", true);
        edit.putExtra("tripID", trips.get(position).getTripId());
        edit.putExtra("position", position);
        startActivity(edit);
    }

    @Override
    public void onStartButtonClicked(int position) {

        Trip trip = trips.get(position);

        trip.setTripStatus(true);
        tripDao.updateTrip(trip);

        Intent answerIntent = new Intent(getContext().getApplicationContext(), MainActivity2.class);
        answerIntent.putExtra("destination",trip.getTripDestination());
        answerIntent.putExtra("TripId",trip.getTripId());

        WorkManager.getInstance(context).cancelAllWorkByTag(trip.getTripId());

        startActivity(answerIntent);


        /*
        context = getActivity().getApplicationContext();;


        WorkManager.getInstance(context).cancelAllWorkByTag(trip.getTripId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, 0);
        } else {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Intent intent = new Intent(activity, Service.class);
            context.startService(intent);
        }

        initBubble();

        GoogleMapsManager googleMapsManager = GoogleMapsManager.getInstance(getContext());
        googleMapsManager.requestPermission();
        if(googleMapsManager.locationPermission){
            GoogleMapsManager.getInstance(getContext()).launchGoogleMaps(trips.get(position).getTripDestination());
        }

        */


        trips = tripDao.selectAllTrips(FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
        rv.setAdapter(new HistoryAdapter(getContext(),trips, (OnRecyclerViewItemClickLister) UpcomingTripsFragment.this));


    }

    @Override
    public void notifyDataChanged(Trip trip, int position) {
        if(position == -1) {
            trips.add(trip);
        }else{
            trips.remove(position);
            trips.add(position, trip);
        }
        rv.getAdapter().notifyDataSetChanged();
    }

}