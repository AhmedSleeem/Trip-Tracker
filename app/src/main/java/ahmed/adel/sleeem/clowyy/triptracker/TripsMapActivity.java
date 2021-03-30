package ahmed.adel.sleeem.clowyy.triptracker;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripCell;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripPoints;

public class TripsMapActivity extends AppCompatActivity  {

    private static final String TAG = "TripsMapActivity";

    GoogleMapsManager googleMapsManager;
    private ImageView myLocationImageView;

    private List<TripCell> tripCellList;

    Dialog lastTripsDialog;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_map);

        googleMapsManager = GoogleMapsManager.getInstance(this);

        googleMapsManager.requestPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMapsManager);
        View mapView = mapFragment.getView();

        myLocationImageView = findViewById(R.id.myLocationImage);

        googleMapsManager.tripPointsList = new ArrayList<>();
        tripCellList = googleMapsManager.tripPointsList;

//
//        Trip(String tripSource, String tripTitle, String tripDestination,
//        boolean tripType, String tripRepeatingType, String tripNotes, String tripMaker,
//                String tripDate, String tripTime, String tripImage, boolean tripStatus)
//
        Trip trip1 = new Trip( "Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh","new trip", "Alexandria", false, "A", "No Notes", "13", "", "","",false);
        Trip trip2 = new Trip( "Alexandria","new ", "Cairo", false, "D", "No Notes", "13", "", "","",false);
        Trip trip3 = new Trip( "Cairo","", "Marsa Matruh, Mersa Matruh", false, "D", "No Notes", "13", "", "","",false);
        Trip trip4 = new Trip( "Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh","", "Military Area, Sidi Barrani", false, "A", "No Notes", "13", "", "","",false);

        TripPoints tripPoints1 = googleMapsManager.getTripPoints(trip1.getTripSource(), trip1.getTripDestination());
        TripPoints tripPoints2 = googleMapsManager.getTripPoints(trip2.getTripSource(), trip2.getTripDestination());
        TripPoints tripPoints3 = googleMapsManager.getTripPoints(trip3.getTripSource(), trip3.getTripDestination());
        TripPoints tripPoints4 = googleMapsManager.getTripPoints(trip4.getTripSource(), trip4.getTripDestination());

        tripCellList.add(new TripCell(trip1, tripPoints1));
        tripCellList.add(new TripCell(trip2, tripPoints2));
        tripCellList.add(new TripCell(trip3, tripPoints3));
        tripCellList.add(new TripCell(trip4, tripPoints4));

        lastTripsDialog = new Dialog(this);
        lastTripsDialog.setContentView(R.layout.last_trips_dialog);

        findViewById(R.id.btnGoToMaps).setOnClickListener(v->{
            // https://maps.googleapis.com/maps/api/directions/json?origin=31.110659299999995,30.938779900000004&destination=31.2000924,29.9187387&key=AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TripExtraInfo tripExtraInfo = getTripExtraInfo(tripPointsList.get(3).start, tripPointsList.get(3).end);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Distance: "+ tripExtraInfo.getDistance() + ", Duration: " + tripExtraInfo.getDuration(), Toast.LENGTH_LONG).show();
                            // 542    7,12
                        }
                    });
                }
            }).start();
            */



            new Thread((new Runnable() {
                @Override
                public void run() {
                    Bitmap locationImage = googleMapsManager.getLocationPhoto("Marsa Matruh, Mersa Matruh");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myLocationImageView.setImageBitmap(locationImage);
                        }
                    });
                }
            })).start();
        });

        findViewById(R.id.fbtnLastTrips).setOnClickListener(v->{
            RecyclerView rvLastTrips = lastTripsDialog.findViewById(R.id.rvLastTrips);
            rvLastTrips.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(lastTripsDialog.getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvLastTrips.setLayoutManager(linearLayoutManager);

            adapter = new LastTripsAdapter(lastTripsDialog.getContext(), tripCellList);
            rvLastTrips.setAdapter(adapter);

            CheckBox cbSelectAll = lastTripsDialog.findViewById(R.id.cbSelectAll);
            cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

                for (TripCell trip: tripCellList) {
                    trip.isSelected = isChecked;
                }

                adapter.notifyDataSetChanged();
            });

            googleMapsManager.pauseCameraAnimation();

            lastTripsDialog.setOnCancelListener(dialog -> {
                googleMapsManager.resumeCameraAnimation();
            });

            lastTripsDialog.findViewById(R.id.btnShow).setOnClickListener(view->{
                lastTripsDialog.dismiss();
                googleMapsManager.startNavigate();
            });

            lastTripsDialog.show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GoogleMapsManager.LOCATION_REQUEST_CODE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    googleMapsManager.locationPermission = true;
                    googleMapsManager.startNavigate();
                }
            }break;
        }
    }
}