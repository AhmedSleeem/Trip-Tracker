package ahmed.adel.sleeem.clowyy.triptracker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips.TripsModel;

public class UpcomingTripsAdapter extends RecyclerView.Adapter<UpcomingTripsAdapter.ViewHandler> {

    private final Context context;
    private static final String TAG= "RecyclerView";
    //private String [] texts;
    private List<Trip> trips;
    //private List<ImageView> imageArr;

    public UpcomingTripsAdapter(Context context , List<Trip> myTrips) {
        this.context = context;
        trips = myTrips;
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

        holder.tripTitle.setText(trips.get(position).getTripTitle());
        holder.start.setText(trips.get(position).getTripSource());
        holder.end.setText(trips.get(position).getTripDestination());
        holder.date.setText(trips.get(position).getTripDate());
        holder.time.setText(trips.get(position).getTripTime());
        //holder.tripImage.setImageBitmap(trips.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHandler extends RecyclerView.ViewHolder {

        public TextView tripTitle;
        public TextView start;
        //        public ImageView tripImage;
//        public ImageView editIcon;
//        public ImageView deleteIcon;
        public TextView end;
        public TextView date;
        public TextView time;
        public View layout;


        public ViewHandler(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            tripTitle = itemView.findViewById(R.id.tripTitle);
            start = itemView.findViewById(R.id.startTxt);
//            tripImage = itemView.findViewById(R.id.tripImage);
//            editIcon = itemView.findViewById(R.id.editIconBtn);
//            deleteIcon = itemView.findViewById(R.id.deleteIconBtn);
            end = itemView.findViewById(R.id.endTxt);
            date = itemView.findViewById(R.id.dateTxt);
            time = itemView.findViewById(R.id.timeTxt);
        }
    }
}

