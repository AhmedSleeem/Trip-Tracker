package ahmed.adel.sleeem.clowyy.triptracker.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class DoneTripReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        String destination = intent.getStringExtra("destination");
        String date = intent.getStringExtra("date");

        Log.i("Done receiver", "onReceive: "+destination);


        TripDao tripDao = TripDatabase.getInstance(context).getTripDao();

        Trip trip = tripDao.selectTripByDate(date);

        trip.setTripStatus(true);

        tripDao.updateTrip(trip);


        LatLng location = GoogleMapsManager.getInstance(context).getLocationFromAddress(destination);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        context.startActivity(mapIntent);



    }
}