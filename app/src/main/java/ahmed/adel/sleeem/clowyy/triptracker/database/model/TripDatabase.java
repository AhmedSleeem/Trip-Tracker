package ahmed.adel.sleeem.clowyy.triptracker.database.model;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Trip.class},version = 3,exportSchema = false)
public abstract class TripDatabase extends RoomDatabase {

    public  abstract TripDao getTripDao();

    private static final String DB_NAME = "tripDatabase.db";
    private static volatile TripDatabase instance;

    public static synchronized TripDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }




    private static TripDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                TripDatabase.class,
                DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }
}
