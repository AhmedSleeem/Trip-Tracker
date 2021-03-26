package ahmed.adel.sleeem.clowyy.triptracker.helpers;

public class TripExtraInfo {
    String distance;
    String duration;

    public TripExtraInfo(String distance, String duration){
        this.distance = distance;
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }
}
