package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import ahmed.adel.sleeem.clowyy.triptracker.adapters.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        HistoryAdapter historyAdapter = new HistoryAdapter(getBaseContext(),new ArrayList<>());




    }
}