package lt.dualpair.android.data.provider.match;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.provider.DbHelper;
import lt.dualpair.android.data.provider.RESTfulContentProvider;

public class MatchContentProvider extends RESTfulContentProvider {

    private static final UriMatcher uriMatcher;
    private static final int NEXT = 1;

    private SQLiteDatabase db;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MatchMeta.AUTHORITY, "next", NEXT);
    }

    @Override
    public boolean onCreate() {
        db = DbHelper.forCurrentUser(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Long userId = AccountUtils.getUserId(getContext());

        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case NEXT:
                cursor = db.rawQuery("" +
                        "SELECT * FROM matches m" +
                        "INNER JOIN match_parties mp1 on mp.match_id = m._id and mp.user_id = ?" +
                        "INNER JOIN match_parties mp2 on mp.match_id = m._id and mp.user_id != ?" +
                        "WHERE mp1.response = 'U'", new String[] {userId.toString(), userId.toString()});
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
