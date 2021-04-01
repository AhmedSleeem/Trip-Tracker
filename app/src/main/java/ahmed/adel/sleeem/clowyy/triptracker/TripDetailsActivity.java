package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;

public class TripDetailsActivity extends AppCompatActivity {

    TextView name,start,end,date,time,distance,duration,speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Trip trip1 = new Trip( "Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh","new trip", "Alexandria", false, "A", "No Notes", "13", "", "","",false, "", "", "");

        name = findViewById(R.id.tripnameTxt);
        start = findViewById(R.id.startpointTxt);
        end = findViewById(R.id.endpointTxt);
        date = findViewById(R.id.tripdateTxt);
        time = findViewById(R.id.triptimeTxt);
        distance = findViewById(R.id.tripdistanceTxt);
        duration = findViewById(R.id.tripdurationTxt);
        speed = findViewById(R.id.tripspeedTxt);

        name.setText(trip1.getTripTitle());
        start.setText(trip1.getTripSource());
        end.setText(trip1.getTripDestination());
        date.setText(trip1.getTripDate());
        time.setText(trip1.getTripTime());
        distance.setText(trip1.getTripDistance());
        duration.setText(trip1.getTripDuration());
        speed.setText(trip1.getTripAvgSpeed());
    }
}