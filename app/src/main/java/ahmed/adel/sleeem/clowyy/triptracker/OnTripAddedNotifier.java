package ahmed.adel.sleeem.clowyy.triptracker;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;

public interface OnTripAddedNotifier {

    public  void notifyDataChanged(Trip trip, int position);
}
