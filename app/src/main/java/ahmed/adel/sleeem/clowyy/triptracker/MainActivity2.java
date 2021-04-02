package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        String destination = getIntent().getStringExtra("destination");
        String tripId = getIntent().getStringExtra("TripId");

        Log.i("Done receiver", "onReceive: "+destination);


        TripDao tripDao = TripDatabase.getInstance(getApplicationContext()).getTripDao();

        Trip trip = tripDao.selectTripById(tripId);

        trip.setTripStatus(true);

        tripDao.updateTrip(trip);


        LatLng location = GoogleMapsManager.getInstance(getApplicationContext()).getLocationFromAddress(destination);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        // mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(mapIntent);
        finish();
    }
}