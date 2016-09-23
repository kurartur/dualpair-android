package lt.dualpair.android.data.provider.user;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public class SearchParameterContentProvider extends ContentProvider {

    private static final String AUTHORITY = "lt.dualpair.android.data.provider.user.SearchParameterContentProvider";

    private static final String SEARCH_PARAMETERS_CONTENT_TYPE = "vnd.android.cursor.item/vnd.lt.dualpair.android.search-parameters";

    private static final int SEARCH_PARAMETERS = 2;

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "me/search-parameters", SEARCH_PARAMETERS);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_PARAMETERS:
                return SEARCH_PARAMETERS_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("unknown type");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
