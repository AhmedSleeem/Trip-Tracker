package ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips;

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

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.UpcomingTripsAdapter;

public class UpcomingTripsFragment extends Fragment {

    //private UpcomingTripsViewModel upcomingTripsViewModel;
    private RecyclerView rv;
    private List<TripsModel> trips;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //upcomingTripsViewModel = new ViewModelProvider(this).get(UpcomingTripsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upcomingtrips, container, false);
//        final TextView textView = root.findViewById(R.id.text_upcomingtrips);
//        upcomingTripsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        TripsModel trip1= new TripsModel("Trip 1","alex","cairo","12/2/2022","12:30 pm","");
        TripsModel trip2= new TripsModel("Trip 2","damietta","alex","22/3/2022","2:30 pm","");
        TripsModel trip3= new TripsModel("Trip 3","kafr el shiekh","cairo","1/3/2022","1:30 pm","");

        trips = Arrays.asList(trip1,trip2,trip3);
        rv = root.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips));


        return root;
    }
}