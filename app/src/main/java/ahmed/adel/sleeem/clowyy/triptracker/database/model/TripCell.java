package ahmed.adel.sleeem.clowyy.triptracker.database.model;

import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripPoints;

public class TripCell {
    public Trip trip;
    public TripPoints tripPoints;
    public boolean isSelected;

    public TripCell(Trip trip, TripPoints tripPoints) {
        this.trip = trip;
        this.tripPoints = tripPoints;
    }

    public TripCell(Trip trip, TripPoints tripPoints, boolean isSelected) {
        this.trip = trip;
        this.tripPoints = tripPoints;
        this.isSelected = isSelected;
    }
}
