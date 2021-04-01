package ahmed.adel.sleeem.clowyy.triptracker.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trips")
public class Trip {

    @PrimaryKey(autoGenerate = true)
    int tripId;

    @ColumnInfo
    String tripSource;

    @ColumnInfo
    String tripTitle;

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }


    @ColumnInfo
    String tripDestination;

    @ColumnInfo
    String tripDistance;

    public Trip(int tripId, String tripSource, String tripTitle, String tripDestination, String tripDistance, String tripDuration, boolean tripType, String tripRepeatingType, String tripNotes, String tripMaker, String tripDate, String tripTime, String tripImage, boolean tripStatus) {
        this.tripId = tripId;
        this.tripSource = tripSource;
        this.tripTitle = tripTitle;
        this.tripDestination = tripDestination;
        this.tripDistance = tripDistance;
        this.tripDuration = tripDuration;
        this.tripType = tripType;
        this.tripRepeatingType = tripRepeatingType;
        this.tripNotes = tripNotes;
        this.tripMaker = tripMaker;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.tripImage = tripImage;
        this.tripStatus = tripStatus;
    }

    public String getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(String tripDistance) {
        this.tripDistance = tripDistance;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(String tripDuration) {
        this.tripDuration = tripDuration;
    }

    @ColumnInfo
    String tripDuration;


    @ColumnInfo
    boolean tripType;//single or round


    @ColumnInfo
    String tripRepeatingType;//not repeating , daily , weekly , monthly ,.....


    @ColumnInfo
    String tripNotes;


    @ColumnInfo
    String tripMaker;// firebaseUserId who is create the trip

    @ColumnInfo
    String tripDate;

    @ColumnInfo
    String tripTime;

    @ColumnInfo
    String tripImage;


    @ColumnInfo
    boolean tripStatus;

    public boolean isTripStatus() {
        return tripStatus;
    }


    public void setTripStatus(boolean tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripImage() {
        return tripImage;
    }

    public void setTripImage(String tripImage) {
        this.tripImage = tripImage;
    }

    public Trip(String tripSource, String tripTitle, String tripDestination,
                boolean tripType, String tripRepeatingType, String tripNotes, String tripMaker,
                String tripDate, String tripTime, String tripImage, boolean tripStatus) {
        this.tripSource = tripSource;
        this.tripTitle = tripTitle;
        this.tripDestination = tripDestination;
        this.tripType = tripType;
        this.tripRepeatingType = tripRepeatingType;
        this.tripNotes = tripNotes;
        this.tripMaker = tripMaker;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.tripImage = tripImage;
        this.tripStatus = tripStatus;
    }

    public Trip() {
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getTripSource() {
        return tripSource;
    }

    public void setTripSource(String tripSource) {
        this.tripSource = tripSource;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public boolean isTripType() {
        return tripType;
    }

    public void setTripType(boolean tripType) {
        this.tripType = tripType;
    }

    public String getTripRepeatingType() {
        return tripRepeatingType;
    }

    public void setTripRepeatingType(String tripRepeatingType) {
        this.tripRepeatingType = tripRepeatingType;
    }

    public String getTripNotes() {
        return tripNotes;
    }

    public void setTripNotes(String tripNotes) {
        this.tripNotes = tripNotes;
    }

    public String getTripMaker() {
        return tripMaker;
    }

    public void setTripMaker(String tripMaker) {
        this.tripMaker = tripMaker;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }
}
