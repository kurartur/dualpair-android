package lt.dualpair.android.data.match;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.resource.Location;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.MatchParty;
import lt.dualpair.android.resource.Photo;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.resource.Sociotype;
import lt.dualpair.android.resource.User;

public class MatchRepository {

    private SQLiteDatabase db;

    public MatchRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Match> next(Long userId) {
        Cursor cursor = db.rawQuery("" +
                "SELECT m._id as match_id," +
                "   m.distance, " +
                "   u.age, " +
                "   u.name, " +
                "   u.description, " +
                "   u._id as user_id," +
                "   mp1._id as user_party_id," +
                "   mp1.response as user_response, " +
                "   mp2._id as opponent_party_id," +
                "   mp2.response as opponent_response " +
                "FROM matches m " +
                "INNER JOIN match_parties mp1 on mp1.match_id = m._id and mp1.user_id = ? " +
                "INNER JOIN match_parties mp2 on mp2.match_id = m._id and mp2.user_id != ? " +
                "INNER JOIN users u on u._id = mp2.user_id " +
                "WHERE mp1.response = 'UNDEFINED' ", new String[] {userId.toString(), userId.toString()});
        List<Match> matches = new ArrayList<>();
        while (cursor.moveToNext()) {
            matches.add(map(cursor));
        }
        return matches;
    }

    public Match one(Long matchId, Long userId) {
        Cursor cursor = db.rawQuery("" +
                "SELECT m._id as match_id," +
                "   m.distance, " +
                "   u.age, " +
                "   u.name, " +
                "   u.description, " +
                "   u._id as user_id," +
                "   mp1._id as user_party_id," +
                "   mp1.response as user_response, " +
                "   mp2._id as opponent_party_id," +
                "   mp2.response as opponent_response " +
                "FROM matches m " +
                "INNER JOIN match_parties mp1 on mp1.match_id = m._id and mp1.user_id = ? " +
                "INNER JOIN match_parties mp2 on mp2.match_id = m._id and mp2.user_id != ? " +
                "INNER JOIN users u on u._id = mp2.user_id " +
                "WHERE m._id=? ", new String[] {userId.toString(), userId.toString(), matchId.toString()});
        if (cursor.moveToNext()) {
            return map(cursor);
        } else {
            return null;
        }
    }

    private Match map(Cursor c) {
        User opponentUser = new User();
        opponentUser.setId(c.getLong(c.getColumnIndex("user_id")));
        opponentUser.setAge(c.getInt(c.getColumnIndex("age")));
        opponentUser.setName(c.getString(c.getColumnIndex("name")));
        opponentUser.setDescription(c.getString(c.getColumnIndex("description")));

        Set<Sociotype> sociotypes = new HashSet<>();
        Cursor sociotypesCursor = db.query("user_sociotypes", null, "user_id=?", new String[]{opponentUser.getId().toString()}, null, null, null);
        while (sociotypesCursor.moveToNext()) {
            Sociotype sociotype = new Sociotype();
            sociotype.setCode1(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code1")));
            sociotype.setCode2(sociotypesCursor.getString(sociotypesCursor.getColumnIndex("code2")));
            sociotypes.add(sociotype);
        }
        opponentUser.setSociotypes(sociotypes);

        List<Photo> photos = new ArrayList<>();
        Cursor photosCursor = db.query("user_photos", null, "user_id=?", new String[]{opponentUser.getId().toString()}, null, null, null);
        while (photosCursor.moveToNext()) {
            Photo photo = new Photo();
            photo.setSourceUrl(photosCursor.getString(photosCursor.getColumnIndex("source_link")));
            photos.add(photo);
        }
        opponentUser.setPhotos(photos);

        Set<Location> locations = new HashSet<>();
        Cursor locationsCursor = db.query("user_locations", null, "user_id=?", new String[]{opponentUser.getId().toString()}, null, null, null);
        while (locationsCursor.moveToNext()) {
            Location location = new Location();
            location.setCity(locationsCursor.getString(locationsCursor.getColumnIndex("city")));
            locations.add(location);
        }
        opponentUser.setLocations(locations);

        MatchParty opponent = new MatchParty();
        opponent.setId(c.getLong(c.getColumnIndex("opponent_party_id")));
        opponent.setResponse(Response.valueOf(c.getString(c.getColumnIndex("opponent_response"))));
        opponent.setUser(opponentUser);

        MatchParty user = new MatchParty();
        user.setId(c.getLong(c.getColumnIndex("user_party_id")));
        user.setResponse(Response.valueOf(c.getString(c.getColumnIndex("user_response"))));

        Match match = new Match();
        match.setId(c.getLong(c.getColumnIndex("match_id")));
        match.setOpponent(opponent);
        match.setUser(user);
        match.setDistance(c.getInt(c.getColumnIndex("distance")));

        return match;
    }

    public void save(Match match, Long userId) {

        Cursor matchCursor = db.query("matches", null, "_id=?", new String[]{match.getId().toString()}, null, null, null);
        if (matchCursor.moveToNext()) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", match.getOpponent().getUser().getId());
        contentValues.put("description", match.getOpponent().getUser().getDescription());
        contentValues.put("age", match.getOpponent().getUser().getAge());
        contentValues.put("name", match.getOpponent().getUser().getName());
        long rowId = db.insert("users", null, contentValues);

        for (Sociotype sociotype : match.getOpponent().getUser().getSociotypes()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", match.getOpponent().getUser().getId());
            contentValues.put("code1", sociotype.getCode1());
            contentValues.put("code2", sociotype.getCode2());
            rowId = db.insert("user_sociotypes", null, contentValues);
        }

        for (Photo photo : match.getOpponent().getUser().getPhotos()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", match.getOpponent().getUser().getId());
            contentValues.put("source_link", photo.getSourceUrl());
            rowId = db.insert("user_photos", null, contentValues);
        }

        for (Location location : match.getOpponent().getUser().getLocations()) {
            contentValues = new ContentValues();
            contentValues.put("user_id", match.getOpponent().getUser().getId());
            contentValues.put("city", location.getCity());
            rowId = db.insert("user_locations", null, contentValues);
        }

        contentValues = new ContentValues();
        contentValues.put("_id", match.getId());
        contentValues.put("distance", match.getDistance());
        contentValues.put("create_time", DbHelper.getDateTimeString(new Date()));
        rowId = db.insert("matches", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("_id", match.getOpponent().getId());
        contentValues.put("match_id", match.getId());
        contentValues.put("user_id", match.getOpponent().getUser().getId());
        contentValues.put("response", Response.UNDEFINED.name());
        rowId = db.insert("match_parties", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("_id", match.getUser().getId());
        contentValues.put("match_id", match.getId());
        contentValues.put("user_id", userId);
        contentValues.put("response", match.getUser().getResponse().name());
        rowId = db.insert("match_parties", null, contentValues);
    }

    public void setResponse(Long partyId, Response response) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("response", response.name());
        db.update("match_parties", contentValues, "_id=?", new String[]{partyId.toString()});
    }

}
