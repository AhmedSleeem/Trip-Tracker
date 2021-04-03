package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class TripDetailsActivity extends AppCompatActivity {

    ImageView tripImage;
    TextView name,start,end,date,time,distance,duration,speed;

    Dialog showNotesDialog;
    Button btnShowNotes, btnOK, btnShowRoute;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        showNotesDialog = new Dialog(this);
        showNotesDialog.setContentView(R.layout.show_notes_dialog);

        String tripID = getIntent().getStringExtra("TripID");

        if(tripID == null && tripID.equals("")){
            finish();
        }

        Trip trip = TripDatabase.getInstance(this).getTripDao().selectTripById(tripID);

        tripImage = findViewById(R.id.imagetrip);
        name = findViewById(R.id.tripnameTxt);
        start = findViewById(R.id.startpointTxt);
        end = findViewById(R.id.endpointTxt);
        date = findViewById(R.id.tripdateTxt);
        time = findViewById(R.id.triptimeTxt);
        distance = findViewById(R.id.tripdistanceTxt);
        duration = findViewById(R.id.tripdurationTxt);
        speed = findViewById(R.id.tripspeedTxt);
        btnOK = findViewById(R.id.okBtn);
        btnShowNotes = findViewById(R.id.shownotesBtn);
        btnShowRoute = findViewById(R.id.btnShowRoute);

        if(trip.getTripImage() != null && !trip.getTripImage().equals("")) {
            Glide.with(getApplicationContext()).load(trip.getTripImage()).into(tripImage);
        }
        else{
            tripImage.setImageResource(R.drawable.defaultimage);
        }

        name.setText(trip.getTripTitle());
        start.setText(trip.getTripSource());
        end.setText(trip.getTripDestination());
        date.setText(trip.getTripDate());
        time.setText(trip.getTripTime());
        distance.setText(trip.getTripDistance());
        duration.setText(trip.getTripDuration());
        speed.setText(trip.getTripAvgSpeed());
        ListView listViewNotes = showNotesDialog.findViewById(R.id.lvShowNotes);
        List<String> notes = excludeNotes(trip.getTripNotes());



        if (notes == null) {
            btnShowNotes.setVisibility(View.GONE);
        }

        if(trip.getTripDistance().equals("N/A")){
            btnShowRoute.setVisibility(View.GONE);
        }

        btnShowNotes.setOnClickListener(v->{
                listViewNotes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes));
                showNotesDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.corner_view));
                showNotesDialog.show();
                showNotesDialog.findViewById(R.id.btnShowNotesOK).setOnClickListener(view -> {
                    showNotesDialog.dismiss();
                });
        });

        btnOK.setOnClickListener(v->{
            finish();
        });

        btnShowRoute.setOnClickListener(v->{
            Intent intent = new Intent(this, TripsMapActivity.class);
            intent.putExtra("Source", trip.getTripSource());
            intent.putExtra("Destination", trip.getTripDestination());
            startActivity(intent);
        });
    }

    List<String> excludeNotes(String note){
        if(note.equals("")){
            return null;
        }
        String[] strings = note.split("Ω");
        List<String>result = new ArrayList<>();
        for(int indx=0;indx<strings.length;++indx){
            result.add(strings[indx].substring(1));
        }

        return result;
    }
}