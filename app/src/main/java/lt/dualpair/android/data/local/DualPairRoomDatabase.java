package lt.dualpair.android.data.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.dao.UserResponseDao;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.SociotypeRelation;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.local.entity.UserResponse;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;

@Database(entities = {
        User.class, UserSociotype.class, UserAccount.class,
        UserResponse.class, UserPhoto.class, Sociotype.class, UserPurposeOfBeing.class,
        UserSearchParameters.class, UserLocation.class, Match.class, SociotypeRelation.class
}, version = 2, exportSchema = false)
@TypeConverters({
        DateTypeConverter.class, RelationshipStatusConverter.class, PurposeOfBeingConverter.class,
        SociotypeCodeConverter.class
})
public abstract class DualPairRoomDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "DualPair";

    public abstract UserDao userDao();

    public abstract UserResponseDao swipeDao();

    public abstract SociotypeDao sociotypeDao();

    public abstract MatchDao matchDao();

    private static DualPairRoomDatabase INSTANCE;

    public static DualPairRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DualPairRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DualPairRoomDatabase.class, DATABASE_NAME)
                            //.fallbackToDestructiveMigration()
                            //.allowMainThreadQueries()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                        new DatabasePopulator(getDatabase(context)).populate();
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE.clearAllTables();
        new DatabasePopulator(INSTANCE).populate();
    }
}
