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

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

private Context context;
private List<Trip>tripList;
private OnRecyclerViewItemClickLister onItemClickListener;





    public HistoryAdapter(Context context, List<Trip> tripList,OnRecyclerViewItemClickLister onItemClickListener) {
        this.context = context;
        this.tripList = tripList;
        this.onItemClickListener=onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.history_cell_row, parent, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Trip trip = tripList.get(position);

        holder.destinationImage.setImageResource(R.drawable.trip);
        holder.title.setText(tripList.get(position).getTripTitle());

        holder.secondary_title.setText(tripList.get(position).getTripSource());
        holder.supplementary_Text.setText(tripList.get(position).getTripDestination());

        if(trip.getTripImage() != null && !trip.getTripImage().equals("")) {
            Glide.with(context).load(trip.getTripImage()).into(holder.destinationImage);
        }
        else{
            holder.destinationImage.setImageResource(R.drawable.defaultimage);
        }


        if (trip.isTripType()){
            holder.tripType.setBackgroundResource(R.drawable.ic_rounded);
        }

        //implement action Buttons



    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView destinationImage;

        TextView title,secondary_title,supplementary_Text;
        MaterialButton actionDelete,actionDetails,tripType;
        public Holder(@NonNull View itemView) {
            super(itemView);

            destinationImage=itemView.findViewById(R.id.destinationImage);
            title=itemView.findViewById(R.id.titleTextView);
            secondary_title=itemView.findViewById(R.id.secondaryTextView);
            supplementary_Text=itemView.findViewById(R.id.supportingText);
            actionDelete=itemView.findViewById(R.id.actionDelete);
            actionDetails=itemView.findViewById(R.id.actionDetails);
            tripType=itemView.findViewById(R.id.tripType);


            actionDetails.setOnClickListener((view)->{
                onItemClickListener.onDetailsIconClicked(getAdapterPosition());
            });


            actionDelete.setOnClickListener((view)->{
                onItemClickListener.onDeleteIconClicked(getAdapterPosition());
            });
        }
    }
}
