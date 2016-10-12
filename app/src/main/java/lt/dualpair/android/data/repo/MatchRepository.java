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
        match.setDistance(c.getInt(c.getColumnIndex(MatchMeta.Match.DISTANCE)));

        return match;
    }

    @Override
    protected Match doSave(Match match) {
        Long matchId = match.getId();

        boolean matchExists = db.query(MatchMeta.Match.TABLE_NAME, new String[]{UserMeta.User._ID}, "_id=?", args(matchId.toString()), null, null, null).moveToNext();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MatchMeta.Match._ID, match.getId());
        contentValues.put(MatchMeta.Match.DISTANCE, match.getDistance());
        contentValues.put(MatchMeta.Match.CREATE_TIME, DatabaseHelper.getDateTimeString(new Date()));

        if (!matchExists) {
            assertOperation(db.insert(MatchMeta.Match.TABLE_NAME, null, contentValues), "Unable to save match " + match);
        } else {
            db.update(MatchMeta.Match.TABLE_NAME, contentValues, "_id=?", args(matchId.toString()));
        }

        saveParty(match.getOpponent(), matchId);
        saveParty(match.getUser(), matchId);

        return match;
    }

    private void saveParty(MatchParty party, Long matchId) {
        db.delete(MatchMeta.Party.TABLE_NAME, "_id=?", args(party.getId().toString()));
        userRepository.save(party.getUser());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MatchMeta.Party._ID, party.getId());
        contentValues.put(MatchMeta.Party.MATCH_ID, matchId);
        contentValues.put(MatchMeta.Party.USER_ID, party.getUser().getId());
        contentValues.put(MatchMeta.Party.RESPONSE, Response.UNDEFINED.name());
        assertOperation(db.insert(MatchMeta.Party.TABLE_NAME, null, contentValues), "Unable to save party " + party);
    }

    public void setResponse(Long partyId, Response response) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("response", response.name());
        db.update("match_parties", contentValues, "_id=?", new String[]{partyId.toString()});
    }

    @Override
    protected void doDelete(Match match) {
        db.delete(MatchMeta.Party.TABLE_NAME, "match_id=?", args(match.getId().toString()));
        db.delete(MatchMeta.Match.TABLE_NAME, "_id=?", args(match.getId().toString()));
        userRepository.delete(match.getOpponent().getUser());
    }

    public void clearNotReviewedMatches(Long userId) {
        for (Match match : next(userId)) {
            delete(match);
        }
    }
}
