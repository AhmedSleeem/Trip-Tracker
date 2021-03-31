package ahmed.adel.sleeem.clowyy.triptracker.helpers;

import com.google.android.gms.maps.model.LatLng;

public class TripPoints {
    public LatLng start;
    public LatLng end;

    public TripPoints(LatLng start, LatLng end){
        this.start = start;
        this.end = end;
    }
}
