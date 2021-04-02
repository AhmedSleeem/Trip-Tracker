package ahmed.adel.sleeem.clowyy.triptracker.database.model;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {


    @Insert
    void insertTrip(Trip ... trip);

    @Delete
    void deleteTrip(Trip ... trip);

    @Query("select * from TRIPS ")
    List<Trip> selectAllTrips();


    @Query("select * from TRIPS  where tripMaker = :user")
    List<Trip> selectAllTrips(String user);

    @Query("select * from TRIPS  where tripId = :tripIdentifier")
    Trip selectTripById(String tripIdentifier);

    @Update
    void updateTrip(Trip trip);

    @Query("delete from trips where tripId = :id ")
    void deleteTripId(String id);



}
