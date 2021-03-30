package ahmed.adel.sleeem.clowyy.triptracker.ui.history;

import android.app.Activity;
import android.content.DialogInterface;
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

        tripList = tripDao.selectAllTrips();
//
//        syncDataWithFirebaseDatabase(tripList);
//


        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(new HistoryAdapter(getContext(), tripList, this));


        return view;
    }

    void syncDataWithFirebaseDatabase(final List<Trip> tripList) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int indx = 0; indx < tripList.size(); ++indx) {

            Trip trip = tripList.get(indx);
            reference.child("trips").child(uid).push().setValue(trip).addOnCompleteListener(task -> {
                Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onDeleteIconClicked(int position) {
        Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();

        deleteAlert(this, position);

    }

    @Override
    public void onDetailsIconClicked(int position) {
        Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
    }


    public void deleteAlert(final HistoryFragment fragment, int position) {


        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Delete");
        //set message
        builder.setMessage("Are you sure you want to Delete ?");
        //positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //call method logout in session class
                //session.logoutUser();
                if (position < tripList.size()) {

                    tripDao.deleteTrip(tripList.get(position));
                    tripList.remove(position);

                    historyRecyclerView.setAdapter(new HistoryAdapter(getContext(), tripList, fragment));


                }

            }
        });
        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }
}