package ahmed.adel.sleeem.clowyy.triptracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class NotesDialog extends AppCompatActivity {

    private RecyclerView rv;
    private List<String> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_dialog);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);


        notes = Arrays.asList("Note 1","Note 2","Note 3","Note 4","Note 5","Note 6","Note 1","Note 2","Note 3","Note 4","Note 5","Note 6","Note 1","Note 2","Note 3","Note 4","Note 5","Note 6");
        rv = findViewById(R.id.recyclerView2);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(new NotesAdapter(getApplicationContext(),notes));


    }


}