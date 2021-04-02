package ahmed.adel.sleeem.clowyy.triptracker.ui.history;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.TripDetailsActivity;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.HistoryAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.OnRecyclerViewItemClickLister;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class HistoryFragment extends Fragment implements OnRecyclerViewItemClickLister {

    private RecyclerView historyRecyclerView;
    private TripDatabase tripDatabase;
    private List<Trip> tripList;

    private TripDao tripDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);


        tripDatabase = TripDatabase.getInstance(getContext());
        tripDao = tripDatabase.getTripDao();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        tripList = tripDao.selectAllTrips(FirebaseAuth.getInstance().getCurrentUser().getUid(),true);
//
//        syncDataWithFirebaseDatabase(tripList);
//


        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(new HistoryAdapter(getContext(), tripList, this));
    }

    void syncDataWithFirebaseDatabase(final List<Trip> tripList) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int indx = 0; indx < tripList.size(); ++indx) {

            Trip trip = tripList.get(indx);
            reference.child("trips").child(uid).push().setValue(trip).addOnCompleteListener(task -> {
                Toast.makeText(getContext(), getString(R.string.DONE), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onDeleteIconClicked(int position) {
        deleteAlert(this, position);
    }

    @Override
    public void onDetailsIconClicked(int position) {
        Intent details = new Intent(getContext(), TripDetailsActivity.class);
        details.putExtra("TripID", tripList.get(position).getTripId());
        startActivity(details);
    }


    public void deleteAlert(final HistoryFragment fragment, int position) {
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle(getString(R.string.deleteMSGtitle));
        //set message
        builder.setMessage(getString(R.string.deleteMSG));
        //positive yes button
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tripID = tripList.get(position).getTripId();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                tripList.remove(position);
                tripDao.deleteTripId(tripID);
                FirebaseDatabase.getInstance().getReference("trips").child(userID).child(tripID).removeValue();
                historyRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        //negative no button
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }
}