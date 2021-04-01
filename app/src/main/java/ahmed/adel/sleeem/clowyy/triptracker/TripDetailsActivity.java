package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
    Button btnShowNotes, btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        showNotesDialog = new Dialog(this);
        showNotesDialog.setContentView(R.layout.show_notes_dialog);

        int tripID = getIntent().getIntExtra("TripID", -1);

        if(tripID == -1){
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

        Glide.with(getApplicationContext()).load(trip.getTripImage()).into(tripImage);
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



        if (notes.size() < 1) {
            btnShowNotes.setVisibility(View.INVISIBLE);

            //MarginLayoutParams params = (MarginLayoutParams) vector8.getLayoutParams();
           // params.width = 200; params.leftMargin = 100; params.topMargin = 200;

         //   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         //   params.setMargins(0, 30, 0 , 0);
         //   btnOK.setLayoutParams(params);
        }

        btnShowNotes.setOnClickListener(v->{
                listViewNotes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes));
                showNotesDialog.show();
                showNotesDialog.findViewById(R.id.btnShowNotesOK).setOnClickListener(view -> {
                    showNotesDialog.dismiss();
                });
        });


        btnOK.setOnClickListener(v->{
            finish();
        });
    }

    List<String> excludeNotes(String note){
        String[] strings = note.split("Ω");
        List<String>result = new ArrayList<>();
        for(int indx=0;indx<strings.length;++indx){
            result.add(strings[indx].substring(1));
        }

        return result;
    }
}