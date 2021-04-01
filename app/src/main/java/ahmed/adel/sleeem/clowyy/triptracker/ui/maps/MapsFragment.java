package ahmed.adel.sleeem.clowyy.triptracker.ui.maps;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.LastTripsAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripCell;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripPoints;

public class MapsFragment extends Fragment {

    GoogleMapsManager googleMapsManager;

    private List<TripCell> tripCellList;

    Dialog lastTripsDialog;

    Context context;

    private RecyclerView.Adapter adapter;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);
        context = root.getContext();

        googleMapsManager = GoogleMapsManager.getInstance(context);

        googleMapsManager.requestPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMapsManager);

        googleMapsManager.tripPointsList = new ArrayList<>();
        tripCellList = googleMapsManager.tripPointsList;

        Trip trip1 = new Trip( "Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh","new trip", "Alexandria", false, "A", "No Notes", "13", "", "","",false, "", "", "");
        Trip trip2 = new Trip( "Alexandria","new ", "Cairo", false, "D", "No Notes", "13", "", "","",false, "", "", "");
        Trip trip3 = new Trip( "Cairo","", "Marsa Matruh, Mersa Matruh", false, "D", "No Notes", "13", "", "","",false, "", "", "");
        Trip trip4 = new Trip( "Kafr El-Shaikh, Qism Kafr El-Shaikh, Kafr Al Sheikh","", "Military Area, Sidi Barrani", false, "A", "No Notes", "13", "", "","",false, "", "", "");
        Trip trip5 = new Trip( "Cairo","", "Military Area, Sidi Barrani", false, "A", "No Notes", "13", "", "","",false, "", "", "");
        Trip trip6 = new Trip( "Cairo","", "Military Area, Sidi Barrani", false, "A", "No Notes", "13", "", "","",false, "", "", "");

        TripPoints tripPoints1 = googleMapsManager.getTripPoints(trip1.getTripSource(), trip1.getTripDestination());
        TripPoints tripPoints2 = googleMapsManager.getTripPoints(trip2.getTripSource(), trip2.getTripDestination());
        TripPoints tripPoints3 = googleMapsManager.getTripPoints(trip3.getTripSource(), trip3.getTripDestination());
        TripPoints tripPoints4 = googleMapsManager.getTripPoints(trip4.getTripSource(), trip4.getTripDestination());
        TripPoints tripPoints5 = googleMapsManager.getTripPoints(trip5.getTripSource(), trip5.getTripDestination());
        TripPoints tripPoints6 = googleMapsManager.getTripPoints(trip6.getTripSource(), trip6.getTripDestination());

        tripCellList.add(new TripCell(trip1, tripPoints1));
        tripCellList.add(new TripCell(trip2, tripPoints2));
        tripCellList.add(new TripCell(trip3, tripPoints3));
        tripCellList.add(new TripCell(trip4, tripPoints4));
        tripCellList.add(new TripCell(trip5, tripPoints5));
        tripCellList.add(new TripCell(trip6, tripPoints6));

        lastTripsDialog = new Dialog(context);
        lastTripsDialog.setContentView(R.layout.last_trips_dialog);
        lastTripsDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.corner_view));

        root.findViewById(R.id.fbtnLastTrips).setOnClickListener(v->{
            RecyclerView rvLastTrips = lastTripsDialog.findViewById(R.id.rvLastTrips);
            rvLastTrips.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(lastTripsDialog.getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvLastTrips.setLayoutManager(linearLayoutManager);

            adapter = new LastTripsAdapter(lastTripsDialog.getContext(), tripCellList);
            rvLastTrips.setAdapter(adapter);

            CheckBox cbSelectAll = lastTripsDialog.findViewById(R.id.cbSelectAll);
            cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                for (TripCell trip: tripCellList) {
                    trip.isSelected = isChecked;
                }

                adapter.notifyDataSetChanged();
            });

            googleMapsManager.pauseCameraAnimation();

            lastTripsDialog.setOnCancelListener(dialog -> {
                googleMapsManager.resumeCameraAnimation();
            });

            lastTripsDialog.findViewById(R.id.btnShow).setOnClickListener(view->{
                lastTripsDialog.dismiss();
                googleMapsManager.startNavigate();
            });

            lastTripsDialog.show();
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GoogleMapsManager.LOCATION_REQUEST_CODE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    googleMapsManager.locationPermission = true;
                    googleMapsManager.startNavigate();
                }
            }break;
        }
    }
}