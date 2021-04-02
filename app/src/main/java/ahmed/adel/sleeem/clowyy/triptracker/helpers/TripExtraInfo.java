package ahmed.adel.sleeem.clowyy.triptracker.helpers;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

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

    public String getAvgSpeed(){
        if(distance.equals("N/A"))
            return "N/A";

        if(distance.equals("N/A"))
            return "N/A";

        double dis = Double.parseDouble(distance.replace(",", "").split(" ")[0]);
        double dur = 1;
        List<String> values = Arrays.asList(duration.split(" "));

        if(duration.contains("day") && duration.contains("hour") && duration.contains("min")){
            dur = Integer.parseInt(values.get(0)) * 24 * 60;
            dur += Integer.parseInt(values.get(2)) * 60;
            dur += Integer.parseInt(values.get(4));
        } else if(duration.contains("day") && duration.contains("hour")){
            dur = Integer.parseInt(values.get(0)) * 24 * 60;
            dur += Integer.parseInt(values.get(2)) * 60;
        }else if(duration.contains("hour") && duration.contains("min")){
            dur = Integer.parseInt(values.get(0)) * 60;
            dur += Integer.parseInt(values.get(2));
        }else if(duration.contains("min")){
            dur = Integer.parseInt(values.get(0));
        }

        dur /= 60;

        return (int)(dis/dur) + " km/h";
    }
}
