package lt.dualpair.android.data.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.data.Repository;
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
        Cursor c = db.query("users", null, "_id=?", new String[]{userId.toString()}, null, null, null);
        if (c.moveToNext()) {
            return map(c);
        } else {
            return null;
        }
    }

    @Override
    protected User doSave(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", user.getId());
        contentValues.put("description", user.getDescription());
        if (user.getDateOfBirth() != null) {
            contentValues.put("date_of_birth", DbHelper.getDateTimeString(user.getDateOfBirth()));
        }
        contentValues.put("age", user.getAge());
        contentValues.put("name", user.getName());
        long rowId = db.insert("users", null, contentValues);

        for (Sociotype sociotype : user.getSociotypes()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", user.getId());
            contentValues.put("code1", sociotype.getCode1());
            contentValues.put("code2", sociotype.getCode2());
            rowId = db.insert("user_sociotypes", null, contentValues);
        }

        for (Photo photo : user.getPhotos()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", user.getId());
            contentValues.put("source_link", photo.getSourceUrl());
            rowId = db.insert("user_photos", null, contentValues);
        }

        for (Location location : user.getLocations()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", user.getId());
            contentValues.put("latitude", location.getLatitude());
            contentValues.put("longitude", location.getLongitude());
            contentValues.put("country_code", location.getCountryCode());
            contentValues.put("city", location.getCity());
            rowId = db.insert("user_locations", null, contentValues);
        }

        for (UserAccount userAccount : user.getAccounts()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", user.getId());
            contentValues.put("account_id", userAccount.getAccountId());
            contentValues.put("account_type", userAccount.getAccountType());
            rowId = db.insert("user_accounts", null, contentValues);
        }

        return user;
    }

    private User map(Cursor c) {
        User user = new User();
        user.setId(c.getLong(c.getColumnIndex("_id")));

        String dateTimeString = c.getString(c.getColumnIndex("date_of_birth"));
        if (dateTimeString != null) {
            user.setDateOfBirth(DbHelper.getDateFromString(dateTimeString));
        }

        user.setAge(c.getInt(c.getColumnIndex("age")));
        user.setName(c.getString(c.getColumnIndex("name")));
        user.setDescription(c.getString(c.getColumnIndex("description")));

        Set<Sociotype> sociotypes = new HashSet<>();
        Cursor sociotypesCursor = db.query(UserMeta.Sociotype.TABLE_NAME, null, "user_id=?", new String[]{user.getId().toString()}, null, null, null);
        while (sociotypesCursor.moveToNext()) {
            Sociotype sociotype = new Sociotype();
            sociotype.setCode1(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code1")));
            sociotype.setCode2(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code2")));
            sociotypes.add(sociotype);
        }
        user.setSociotypes(sociotypes);

        List<Photo> photos = new ArrayList<>();
        Cursor photosCursor = db.query(UserMeta.Photo.TABLE_NAME, null, "user_id=?", new String[]{user.getId().toString()}, null, null, null);
        while (photosCursor.moveToNext()) {
            Photo photo = new Photo();
            photo.setSourceUrl(photosCursor.getString(photosCursor.getColumnIndex("source_link")));
            photos.add(photo);
        }
        user.setPhotos(photos);

        Set<Location> locations = new HashSet<>();
        Cursor locationsCursor = db.query(UserMeta.Location.TABLE_NAME, null, "user_id=?", new String[]{user.getId().toString()}, null, null, null);
        while (locationsCursor.moveToNext()) {
            Location location = new Location();
            location.setLatitude(locationsCursor.getDouble(locationsCursor.getColumnIndex("latitude")));
            location.setLongitude(locationsCursor.getDouble(locationsCursor.getColumnIndex("longitude")));
            location.setCountryCode(locationsCursor.getString(locationsCursor.getColumnIndex("country_code")));
            location.setCity(locationsCursor.getString(locationsCursor.getColumnIndex("city")));
            locations.add(location);
        }
        user.setLocations(locations);

        return user;
    }

    @Override
    protected void doDelete(User user) {
        String userId = user.getId().toString();
        db.delete(UserMeta.Location.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.Photo.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.Sociotype.TABLE_NAME, "user_id=?", args(userId));
        db.delete(UserMeta.User.TABLE_NAME, "_id=?", args(userId));
    }
}
