package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ahmed.adel.sleeem.clowyy.triptracker.adapters.HistoryAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView historyRecyclerView;
    TripDatabase tripDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        HistoryAdapter historyAdapter = new HistoryAdapter(getBaseContext(), new ArrayList<>());


        tripDatabase = TripDatabase.getInstance(getApplicationContext());
        TripDao tripDao = tripDatabase.getTripDao();

        List<Trip> tripList = tripDao.selectAllTrips();
//
//        syncDataWithFirebaseDatabase(tripList);
//


        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        historyRecyclerView.setAdapter(new HistoryAdapter(getBaseContext(), tripList));


    }

    void syncDataWithFirebaseDatabase(final List<Trip> tripList) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int indx = 0; indx < tripList.size(); ++indx) {

            Trip trip = tripList.get(indx);
            reference.child("trips").child(uid).push().setValue(trip).addOnCompleteListener(task -> {
                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            });
        }
    }


}