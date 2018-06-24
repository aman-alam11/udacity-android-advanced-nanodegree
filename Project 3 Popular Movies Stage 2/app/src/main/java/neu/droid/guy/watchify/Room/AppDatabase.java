package neu.droid.guy.watchify.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import neu.droid.guy.watchify.POJO.FavMovies;

@Database(entities = {FavMovies.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private final static String DATABASE_NAME = "watchifydb";
    private static final Object LOCK = new Object();
    private static AppDatabase dbInstance;

    /** Follow Th Singleton pattern */
    public static AppDatabase getInstance(Context context) {
        if (dbInstance == null) {

            synchronized (LOCK) {
                dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return dbInstance;
    }


    public abstract FavMoviesDAO moviesDAO();


}
