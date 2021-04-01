package ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.TripActivity;
import ahmed.adel.sleeem.clowyy.triptracker.TripDetailsActivity;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.UpcomingTripsAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class UpcomingTripsFragment extends Fragment implements OnUpcomingAdapterItemClicked {

    private RecyclerView rv;
    private List<Trip> trips;

    @Override
    public void onStart() {
        super.onStart();
        trips = TripDatabase.getInstance(getContext()).getTripDao().selectAllTrips();

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips,this));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upcomingtrips, container, false);


        rv = root.findViewById(R.id.recyclerView);

        return root;
    }

    @Override
    public void onDetailsIconClicked(int position) {
        Intent details = new Intent(getContext(), TripDetailsActivity.class);
        startActivity(details);
    }

    @Override
    public void onDeleteIconClicked(int position) {

    }

    @Override
    public void onEditIconClicked(int position) {
        Intent details = new Intent(getContext(), TripActivity.class);
        startActivity(details);

    }

    @Override
    public void onStartButtonClicked(int position) {
        GoogleMapsManager googleMapsManager = GoogleMapsManager.getInstance(getContext());
        googleMapsManager.requestPermission();
        if(googleMapsManager.locationPermission){
            GoogleMapsManager.getInstance(getContext()).launchGoogleMaps(trips.get(position).getTripDestination());
        }

    }
}