package ahmed.adel.sleeem.clowyy.triptracker;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.NoteState;

public class AddNotesDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes_dialog);

        String tripID = getIntent().getStringExtra("tripID");

        Trip trip = TripDatabase.getInstance(this).getTripDao().selectTripById(tripID);

        this.setTitle(trip.getTripTitle());

        StringBuilder notes = new StringBuilder(trip.getTripNotes());

        EditText editText = findViewById(R.id.addnoteTxt);

        findViewById(R.id.addNoteBtn).setOnClickListener(v -> {
            String n = editText.getText().toString();
            if (n.length() > 0) {
                notes.append("0" + n + "Ω");
            }

            trip.setTripNotes(notes.toString());
            TripDatabase.getInstance(getApplicationContext()).getTripDao().updateTrip(trip);
            MainActivity2.add();
            finish();
        });

    }
}