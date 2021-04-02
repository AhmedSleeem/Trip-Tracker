package ahmed.adel.sleeem.clowyy.triptracker;

import android.app.Dialog;
import android.content.Intent;
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
import ahmed.adel.sleeem.clowyy.triptracker.service.MyService;

public class TripsMapActivity extends AppCompatActivity  {

    private static final String TAG = "TripsMapActivity";

    GoogleMapsManager googleMapsManager;

    private List<TripCell> tripCellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_map);

        String source = getIntent().getStringExtra("Source");
        String destination = getIntent().getStringExtra("Destination");

        googleMapsManager = GoogleMapsManager.getInstance(getApplicationContext());

        googleMapsManager.requestPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMapsManager);

        googleMapsManager.tripPointsList = new ArrayList<>();
        tripCellList = googleMapsManager.tripPointsList;

        Trip trip = new Trip(source, destination);

        TripPoints tripPoints = googleMapsManager.getTripPoints(trip.getTripSource(), trip.getTripDestination());

        tripCellList.add(new TripCell(trip, tripPoints, true));
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