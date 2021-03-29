package ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips;

public class TripsModel {
    private String tripTitle;
    private String start;
    private String end;
    private String date;
    private String time;
    private String image;

    public TripsModel() {
        this.date="";
        this.time="";
        this.image="";
        this.end="";
        this.start="";
        this.tripTitle="";
    }

    public TripsModel(String tripTitle, String start, String end, String date, String time, String image) {
        this.tripTitle = tripTitle;
        this.start = start;
        this.end = end;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

