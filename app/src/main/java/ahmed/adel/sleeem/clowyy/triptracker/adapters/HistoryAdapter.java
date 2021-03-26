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

import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

private Context context;
private List<Trip>tripList;
private OnItemClickListener onItemClickListener;

public  interface OnItemClickListener{
    void onDetailIconClicked(int position);
    void onDeleteIconClicked(int position);
}

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public HistoryAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.history_cell_row, parent, false);
        return new Holder(inflate,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.destinationImage.setImageResource(R.drawable.trip);
        holder.title.setText(tripList.get(position).getTripTitle());

        holder.secondary_title.setText(tripList.get(position).getTripSource());
        holder.supplementary_Text.setText(tripList.get(position).getTripDestination());

        //implement aCtion Buttons




    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView destinationImage;

        TextView title,secondary_title,supplementary_Text;
        Button actionDelete,actionDetails;
        public Holder(@NonNull View itemView,final OnItemClickListener onItemClickListener) {
            super(itemView);

            destinationImage=itemView.findViewById(R.id.destinationImage);
            title=itemView.findViewById(R.id.titleTextView);
            secondary_title=itemView.findViewById(R.id.secondaryTextView);
            supplementary_Text=itemView.findViewById(R.id.supportingText);
            actionDelete=itemView.findViewById(R.id.actionDelete);
            actionDetails=itemView.findViewById(R.id.actionDetails);
        }
    }
}
