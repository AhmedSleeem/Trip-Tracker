package ahmed.adel.sleeem.clowyy.triptracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.NoteState;

public class NotesDialog extends AppCompatActivity {

    private RecyclerView rv;
    private List<NoteState> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_dialog);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        String tripID = getIntent().getStringExtra("tripID");

        Trip trip = TripDatabase.getInstance(this).getTripDao().selectTripById(tripID);

        List<String> excludeNotes = excludeNotes(trip.getTripNotes());
        notes = new ArrayList<>();

        for (String n : excludeNotes){
            NoteState noteState = new NoteState();
            if(n.charAt(0) == '0'){
                noteState.setChecked(false);
            }else{
                noteState.setChecked(true);
            }

            noteState.setNote(n.substring(1));
            notes.add(noteState);
        }

        rv = findViewById(R.id.recyclerView2);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(new NotesAdapter(getApplicationContext(), notes));

        findViewById(R.id.saveBtn).setOnClickListener(v->{
            StringBuilder stringBuilder = new StringBuilder();

            for(NoteState noteState: notes){
                stringBuilder.append((noteState.isChecked()? "1": "0") + noteState.getNote() + "Ω");
            }

            trip.setTripNotes(stringBuilder.toString());

            TripDatabase.getInstance(getApplicationContext()).getTripDao().updateTrip(trip);
            finish();
        });
    }

    List<String> excludeNotes(String note) {
        if (note.equals("")) {
            return new ArrayList<>();
        }

        String[] strings = note.split("Ω");
        List<String> result = new ArrayList<>();
        for (int indx = 0; indx < strings.length; ++indx) {
            result.add(strings[indx]);
        }

        return result;
    }
}