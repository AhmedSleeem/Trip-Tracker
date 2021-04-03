package ahmed.adel.sleeem.clowyy.triptracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.GoogleMapsManager;
import ahmed.adel.sleeem.clowyy.triptracker.OnTripAddedNotifier;
import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips.OnUpcomingAdapterItemClicked;

public class UpcomingTripsAdapter extends RecyclerView.Adapter<UpcomingTripsAdapter.ViewHandler> {

    private final Context context;
    private static final String TAG= "RecyclerView";
    //private String [] texts;
    private List<Trip> trips;
    //private List<ImageView> imageArr;

    OnUpcomingAdapterItemClicked onUpcomingAdapterItemClicked;

    public UpcomingTripsAdapter(Context context , List<Trip> myTrips,OnUpcomingAdapterItemClicked onUpcomingAdapterItemClicked) {
        this.context = context;
        trips = myTrips;

        this.onUpcomingAdapterItemClicked = onUpcomingAdapterItemClicked;
    }

    @NonNull
    @Override
    public UpcomingTripsAdapter.ViewHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.upcomingtrip_cardview,parent,false);
        ViewHandler vh = new ViewHandler(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingTripsAdapter.ViewHandler holder, int position) {
        Trip trip = trips.get(position);

        holder.tripTitle.setText(trip.getTripTitle());
        holder.start.setText(trip.getTripSource());
        holder.end.setText(trip.getTripDestination());
        holder.date.setText(trip.getTripDate());
        holder.time.setText(trip.getTripTime());
        holder.distance.setText(trip.getTripDistance());
        holder.duration.setText(trip.getTripDuration());
        holder.avgSpeed.setText(trip.getTripAvgSpeed());

        if(trip.getTripImage() != null && !trip.getTripImage().equals("")) {
            Glide.with(context).load(trip.getTripImage()).into(holder.tripImage);
        }

        if (trip.isTripType()){
            holder.tripTypeBtn.setBackgroundResource(R.drawable.ic_rounded);
        }

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHandler extends RecyclerView.ViewHolder {
        ImageView tripImage;
        TextView tripTitle,start,end,date,time,distance,duration,avgSpeed;
        MaterialButton deleteBtn,editBtn,viewBtn,tripTypeBtn;
        Button startBtn;

        public ViewHandler(@NonNull View itemView) {
            super(itemView);

            tripImage = itemView.findViewById(R.id.tripImage);
            tripTitle = itemView.findViewById(R.id.tripTitle);
            start = itemView.findViewById(R.id.startTxt);
            end = itemView.findViewById(R.id.endTxt);
            date = itemView.findViewById(R.id.dateTxt);
            time = itemView.findViewById(R.id.timeTxt);
            distance = itemView.findViewById(R.id.distanceTxt);
            duration = itemView.findViewById(R.id.durationTxt);
            avgSpeed = itemView.findViewById(R.id.averageSpeedTxt);

            editBtn = itemView.findViewById(R.id.editIconBtn);
            deleteBtn = itemView.findViewById(R.id.deleteIconBtn);
            viewBtn = itemView.findViewById(R.id.viewIconBtn);
            tripTypeBtn = itemView.findViewById(R.id.tripType);
            startBtn = itemView.findViewById(R.id.startBtn);

            editBtn.setOnClickListener(v->{
                onUpcomingAdapterItemClicked.onEditIconClicked(getAdapterPosition());
            });

            deleteBtn.setOnClickListener(v->{
                onUpcomingAdapterItemClicked.onDeleteIconClicked(getAdapterPosition());
            });

            viewBtn.setOnClickListener(v->{
                onUpcomingAdapterItemClicked.onDetailsIconClicked(getAdapterPosition());
            });

            startBtn.setOnClickListener(v->{
                onUpcomingAdapterItemClicked.onStartButtonClicked(getAdapterPosition());
            });
        }
    }
}

