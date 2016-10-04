package lt.dualpair.android.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.User;

public class MatchRepository extends Repository<Match> {

    private static final String MATCH_QUERY = "" +
            "SELECT m._id as match_id," +
            "   m.distance," +
            "   mp1._id as user_party_id," +
            "   mp1.user_id as user_id, " +
            "   mp1.response as user_response, " +
            "   mp2._id as opponent_party_id," +
            "   mp2.response as opponent_response, " +
            "   mp2.user_id as opponent_id " +
            "FROM matches m " +
            "INNER JOIN match_parties mp1 on mp1.match_id = m._id and mp1.user_id = ? " +
            "INNER JOIN match_parties mp2 on mp2.match_id = m._id and mp2.user_id != ? " +
            "INNER JOIN users u on u._id = mp2.user_id ";

    private UserRepository userRepository;

    public MatchRepository(SQLiteDatabase db) {
        super(db);
        userRepository = new UserRepository(db);
    }

    public List<Match> next(Long userId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(MATCH_QUERY +
                    "WHERE mp1.response = 'UNDEFINED' ", args(userId.toString(), userId.toString()));
            List<Match> matches = new ArrayList<>();
            while (cursor.moveToNext()) {
                matches.add(mapMatch(cursor));
            }
            return matches;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Match findOne(Long matchId, Long userId) {
        Cursor cursor = db.rawQuery(MATCH_QUERY +
                "WHERE m._id=? ", args(userId.toString(), userId.toString(), matchId.toString()));
        if (cursor.moveToNext()) {
            return mapMatch(cursor);
        } else {
            return null;
        }
    }

    private Match mapMatch(Cursor c) {
        User opponent = userRepository.get(c.getLong(c.getColumnIndex("opponent_id")));
        MatchParty opponentParty = new MatchParty();
        opponentParty.setId(c.getLong(c.getColumnIndex("opponent_party_id")));
        opponentParty.setResponse(Response.valueOf(c.getString(c.getColumnIndex("opponent_response"))));
        opponentParty.setUser(opponent);

        User user = userRepository.get(c.getLong(c.getColumnIndex("user_id")));
        MatchParty userParty = new MatchParty();
        userParty.setId(c.getLong(c.getColumnIndex("user_party_id")));
        userParty.setResponse(Response.valueOf(c.getString(c.getColumnIndex("user_response"))));
        userParty.setUser(user);

        Match match = new Match();
        match.setId(c.getLong(c.getColumnIndex("match_id")));
        match.setOpponent(opponentParty);
        match.setUser(userParty);
        match.setDistance(c.getInt(c.getColumnIndex("distance")));

        return match;
    }

    private MatchParty mapParty(Cursor c) {
        User user = userRepository.get(c.getLong(c.getColumnIndex("user_id")));
        MatchParty matchParty = new MatchParty();
        matchParty.setId(c.getLong(c.getColumnIndex(MatchMeta.Party._ID)));
        matchParty.setResponse(Response.valueOf(c.getString(c.getColumnIndex(MatchMeta.Party.RESPONSE))));
        matchParty.setUser(user);
        return matchParty;
    }

    @Override
    protected Match doSave(Match match) {
        Long matchId = match.getId();
        Cursor matchCursor = null;
        try {
            matchCursor = db.rawQuery(MATCH_QUERY + "WHERE m._id=?", args(matchId.toString()));
            if (matchCursor.moveToNext()) {
                // delete and save again
                matchCursor.close();

                db.delete(MatchMeta.Party.TABLE_NAME, "match_id=?", args(match.getId().toString()));
                db.delete(MatchMeta.Match.TABLE_NAME, "_id=?", args(match.getId().toString()));
                userRepository.delete(match.getOpponent().getUser());
            }
            userRepository.save(match.getOpponent().getUser());
        } finally {
            if (matchCursor != null && !matchCursor.isClosed()) {
                matchCursor.close();
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", match.getId());
        contentValues.put("distance", match.getDistance());
        contentValues.put("create_time", DatabaseHelper.getDateTimeString(new Date()));
        long rowId = db.insert("matches", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("_id", match.getOpponent().getId());
        contentValues.put("match_id", match.getId());
        contentValues.put("user_id", match.getOpponent().getUser().getId());
        contentValues.put("response", Response.UNDEFINED.name());
        rowId = db.insert("match_parties", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("_id", match.getUser().getId());
        contentValues.put("match_id", match.getId());
        contentValues.put("user_id", match.getUser().getUser().getId());
        contentValues.put("response", match.getUser().getResponse().name());
        rowId = db.insert("match_parties", null, contentValues);

        return match;
    }

    public void setResponse(Long partyId, Response response) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("response", response.name());
        db.update("match_parties", contentValues, "_id=?", new String[]{partyId.toString()});
    }

    @Override
    protected void doDelete(Match match) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
