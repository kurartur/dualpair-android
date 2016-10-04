package lt.dualpair.android.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;

public class UserRepository extends Repository<User> {

    public UserRepository(SQLiteDatabase db) {
        super(db);
    }

    public User get(Long userId) {
        Cursor c = null;
        try {
            c = db.query(UserMeta.User.TABLE_NAME, null, "_id=?", args(userId.toString()), null, null, null);

            if (c.moveToNext()) {
                return map(c);
            } else {
                return null;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    protected User doSave(User user) {
        Long userId = user.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserMeta.User._ID, user.getId());
        contentValues.put(UserMeta.User.DESCRIPTION, user.getDescription());
        if (user.getDateOfBirth() != null) {
            contentValues.put(UserMeta.User.DATE_OF_BIRTH, DatabaseHelper.getDateTimeString(user.getDateOfBirth()));
        }
        contentValues.put(UserMeta.User.AGE, user.getAge());
        contentValues.put(UserMeta.User.NAME, user.getName());

        boolean userExists = db.query(UserMeta.User.TABLE_NAME, new String[]{"_id"}, "_id=?", new String[]{userId.toString()}, null, null, null).moveToNext();

        if (!userExists) {
            assertOperation(db.insert("users", null, contentValues), "Unable to insert user " + user);
            insertSociotypes(userId, user.getSociotypes());
            insertPhotos(userId, user.getPhotos());
            insertLocations(userId, user.getLocations());
            insertAccounts(userId, user.getAccounts());
        } else {
            db.update(UserMeta.User.TABLE_NAME, contentValues, "_id=?", args(userId.toString()));
            db.delete(UserMeta.Sociotype.TABLE_NAME, "user_id=?", args(userId.toString()));
            insertSociotypes(userId, user.getSociotypes());
            db.delete(UserMeta.Photo.TABLE_NAME, "user_id=?", args(userId.toString()));
            insertPhotos(userId, user.getPhotos());
            db.delete(UserMeta.Location.TABLE_NAME, "user_id=?", args(userId.toString()));
            insertLocations(userId, user.getLocations());
            db.delete(UserMeta.UserAccount.TABLE_NAME, "user_id=?", args(userId.toString()));
            insertAccounts(userId, user.getAccounts());
        }

        return user;
    }

    private void insertSociotypes(Long userId, Set<Sociotype> sociotypes) {
        for (Sociotype sociotype : sociotypes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userId);
            contentValues.put("code1", sociotype.getCode1());
            contentValues.put("code2", sociotype.getCode2());
            assertOperation(db.insert("user_sociotypes", null, contentValues), "Unable to insert sociotype: " + sociotype);
        }
    }

    private void insertPhotos(Long userId, List<Photo> photos) {
        for (Photo photo : photos) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userId);
            contentValues.put("source_link", photo.getSourceUrl());
            assertOperation(db.insert("user_photos", null, contentValues), "Unable to insert photo " + photo);
        }
    }

    private void insertLocations(Long userId, Set<Location> locations) {
        for (Location location : locations) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userId);
            contentValues.put("latitude", location.getLatitude());
            contentValues.put("longitude", location.getLongitude());
            contentValues.put("country_code", location.getCountryCode());
            contentValues.put("city", location.getCity());
            assertOperation(db.insert("user_locations", null, contentValues), "Unable to insert location " + location);
        }

    }

    private void insertAccounts(Long userId, List<UserAccount> userAccounts) {
        if (userAccounts!= null) {
            for (UserAccount userAccount : userAccounts) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_id", userId);
                contentValues.put("account_id", userAccount.getAccountId());
                contentValues.put("account_type", userAccount.getAccountType());
                assertOperation(db.insert("user_accounts", null, contentValues), "Unable to insert account " + userAccount);
            }
        }
    }

    private User map(Cursor c) {
        User user = new User();

        Long userId = c.getLong(c.getColumnIndex(UserMeta.User._ID));
        user.setId(userId);

        String dateTimeString = c.getString(c.getColumnIndex("date_of_birth"));
        if (dateTimeString != null) {
            user.setDateOfBirth(DatabaseHelper.getDateFromString(dateTimeString));
        }

        user.setAge(c.getInt(c.getColumnIndex("age")));
        user.setName(c.getString(c.getColumnIndex("name")));
        user.setDescription(c.getString(c.getColumnIndex("description")));

        user.setSociotypes(getSociotypes(userId));
        user.setPhotos(getPhotos(userId));
        user.setLocations(getLocations(userId));

        return user;
    }

    private Set<Sociotype> getSociotypes(Long userId) {
        Cursor sociotypesCursor = null;
        try {
            Set<Sociotype> sociotypes = new HashSet<>();
            sociotypesCursor = db.query(UserMeta.Sociotype.TABLE_NAME, null, "user_id=?", new String[]{userId.toString()}, null, null, null);
            while (sociotypesCursor.moveToNext()) {
                Sociotype sociotype = new Sociotype();
                sociotype.setCode1(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code1")));
                sociotype.setCode2(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code2")));
                sociotypes.add(sociotype);
            }
            return sociotypes;
        } finally {
            if (sociotypesCursor != null) {
                sociotypesCursor.close();
            }
        }
    }

    private List<Photo> getPhotos(Long userId) {
        Cursor photosCursor = null;
        try {
            List<Photo> photos = new ArrayList<>();
            photosCursor = db.query(UserMeta.Photo.TABLE_NAME, null, "user_id=?", new String[]{userId.toString()}, null, null, null);
            while (photosCursor.moveToNext()) {
                Photo photo = new Photo();
                photo.setSourceUrl(photosCursor.getString(photosCursor.getColumnIndex("source_link")));
                photos.add(photo);
            }
            return photos;
        } finally {
            if (photosCursor != null) {
                photosCursor.close();
            }
        }
    }

    private Set<Location> getLocations(Long userId) {
        Cursor locationsCursor = null;
        try {
            Set<Location> locations = new HashSet<>();
            locationsCursor = db.query(UserMeta.Location.TABLE_NAME, null, "user_id=?", new String[]{userId.toString()}, null, null, null);
            while (locationsCursor.moveToNext()) {
                Location location = new Location();
                location.setLatitude(locationsCursor.getDouble(locationsCursor.getColumnIndex("latitude")));
                location.setLongitude(locationsCursor.getDouble(locationsCursor.getColumnIndex("longitude")));
                location.setCountryCode(locationsCursor.getString(locationsCursor.getColumnIndex("country_code")));
                location.setCity(locationsCursor.getString(locationsCursor.getColumnIndex("city")));
                locations.add(location);
            }
            return locations;
        } finally {
            if (locationsCursor != null) {
                locationsCursor.close();
            }
        }
    }

    @Override
    protected void doDelete(User user) {
        String userId = user.getId().toString();
        db.delete(UserMeta.Location.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.Photo.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.Sociotype.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.User.TABLE_NAME, "_id=?", args(userId));
    }

    private void assertOperation(long rowId, String message) {
        if (rowId == -1) throw new RepositoryException(message);
    }
}
