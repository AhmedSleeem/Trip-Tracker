package ahmed.adel.sleeem.clowyy.triptracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripCell;

public class LastTripsAdapter extends RecyclerView.Adapter<LastTripsAdapter.ViewHolder> {
    private final Context context;
    private List<TripCell> tripList;

    public LastTripsAdapter(Context context, List<TripCell> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public LastTripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View myCell = inflater.inflate(R.layout.trip_cell, parent, false);

        ViewHolder holder = new ViewHolder(myCell);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LastTripsAdapter.ViewHolder holder, int position) {
        holder.tvSource.setText(tripList.get(position).trip.getTripSource());
        holder.tvDestination.setText(tripList.get(position).trip.getTripDestination());
        holder.cbSelected.setChecked(tripList.get(position).isSelected);

        holder.cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.tripList.get(position).isSelected = isChecked;
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvDestination;
        CheckBox cbSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            cbSelected = itemView.findViewById(R.id.cbSelected);

            itemView.setOnClickListener(v->{
                cbSelected.setChecked(!cbSelected.isChecked());
            });
        }
    }
}
