package lt.dualpair.android.data.provider.user;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.Date;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.core.user.GetUserPrincipalTask;
import lt.dualpair.android.data.provider.DbHelper;
import lt.dualpair.android.data.provider.RESTfulContentProvider;
import lt.dualpair.android.resource.Photo;
import lt.dualpair.android.resource.Sociotype;
import lt.dualpair.android.rx.EmptySubscriber;

public class UserContentProvider extends RESTfulContentProvider {

    public static final String USER_TABLE_NAME = "user";
    public static final String SOCIOTYPES_TABLE_NAME = "user_sociotypes";
    public static final String SEARCH_PARAMETERS_TABLE_NAME = "search_parameters";
    public static final String ACCOUNTS_TABLE_NAME = "user_accounts";
    public static final String PHOTOS_TABLE_NAME = "user_photos";

    private static final int USER = 1;
    private static final int SOCIOTYPES = 2;
    private static final int SEARCH_PARAMETERS = 3;
    private static final int ACCOUNTS = 4;
    private static final int PHOTOS = 5;

    private static UriMatcher uriMatcher;

    private DbHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(User.AUTHORITY, "user", USER);
        uriMatcher.addURI(User.AUTHORITY, "sociotypes", SOCIOTYPES);
        uriMatcher.addURI(User.AUTHORITY, "search_parameters", SEARCH_PARAMETERS);
        uriMatcher.addURI(User.AUTHORITY, "accounts", ACCOUNTS);
        uriMatcher.addURI(User.AUTHORITY, "photos", PHOTOS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext(), "1");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, final String sortOrder) {

        int match = uriMatcher.match(uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor queryCursor;

        String userId = AccountUtils.getUserId(getContext()).toString();

        switch (match) {
            case USER:
                queryCursor = db.query(USER_TABLE_NAME, projection, "_id=?", new String[]{userId}, null, null, sortOrder);
                break;
            case SOCIOTYPES:
                queryCursor = db.query(SOCIOTYPES_TABLE_NAME, projection, "user_id=?", new String[]{userId}, null, null, sortOrder);
                break;
            case PHOTOS:
                queryCursor = db.query(PHOTOS_TABLE_NAME, projection, "user_id=?", new String[]{userId}, null, null, sortOrder);
                break;
            case SEARCH_PARAMETERS:
                queryCursor = db.query(SEARCH_PARAMETERS_TABLE_NAME, projection, "user_id=?", new String[]{userId}, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri");
        }

        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Long lastUpdate = sharedPreferences.getLong("USER_LAST_UPDATE", 0L);
        if (((new Date().getTime() - lastUpdate) / 1000 / 60) > 5) {
            new GetUserPrincipalTask(getContext()).execute(new EmptySubscriber<lt.dualpair.android.resource.User>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(lt.dualpair.android.resource.User user) {
                    ContentValues values = new ContentValues();
                    values.put(User.UserColumns._ID, user.getId());
                    values.put(User.UserColumns.NAME, user.getName());
                    values.put(User.UserColumns.DATE_OF_BIRTH, DbHelper.getDateTimeString(user.getDateOfBirth()));
                    values.put(User.UserColumns.AGE, user.getAge());
                    values.put(User.UserColumns.DESCRIPTION, user.getDescription());
                    insert(User.UserColumns.USER_URI, values, dbHelper.getWritableDatabase());

                    for (Sociotype sociotype : user.getSociotypes()) {
                        values = new ContentValues();
                        values.put(User.SociotypeColumns.CODE_1, sociotype.getCode1());
                        values.put(User.SociotypeColumns.CODE_2, sociotype.getCode2());
                        values.put(User.SociotypeColumns.USER_ID, user.getId());
                        insert(User.SociotypeColumns.SOCIOTYPES_URI, values, dbHelper.getWritableDatabase());
                    }

                    for (Photo photo : user.getPhotos()) {
                        values = new ContentValues();
                        values.put(User.PhotoColumns.USER_ID, user.getId());
                        values.put(User.PhotoColumns.SOURCE_LINK, photo.getSourceUrl());
                        insert(User.PhotoColumns.PHOTOS_URI, values, dbHelper.getWritableDatabase());
                    }

                    sharedPreferences.edit().putLong("USER_LAST_UPDATE", new Date().getTime()).commit();
                }
            });
        }

        return queryCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USER:
                return User.UserColumns.CONTENT_TYPE;
            case SOCIOTYPES:
                return User.SociotypeColumns.CONTENT_SOCIOTYPES_TYPE;
            case PHOTOS:
                return User.PhotoColumns.CONTENT_PHOTOS_TYPE;
            default:
                throw new IllegalArgumentException("unknown type");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    private Uri insert(Uri uri, ContentValues values, SQLiteDatabase db) {
        long rowId;
        switch (uriMatcher.match(uri)) {
            case USER:
                db.delete(USER_TABLE_NAME, null, null);
                rowId = db.insert(USER_TABLE_NAME, null, values);
                if (rowId >= 0) {
                    getContext().getContentResolver().notifyChange(User.UserColumns.USER_URI, null);
                    return uri;
                }

                throw new IllegalStateException("could not insert " +
                        "content values: " + values);

            case SOCIOTYPES:
                db.delete(SOCIOTYPES_TABLE_NAME, null, null);
                rowId = db.insert(SOCIOTYPES_TABLE_NAME, null, values);
                if (rowId >= 0) {
                    getContext().getContentResolver().notifyChange(User.SociotypeColumns.SOCIOTYPES_URI, null);
                    return uri;
                }

                throw new IllegalStateException("could not insert " +
                        "content values: " + values);

            case PHOTOS:
                db.delete(PHOTOS_TABLE_NAME, null, null);
                rowId = db.insert(PHOTOS_TABLE_NAME, null, values);
                if (rowId >= 0) {
                    getContext().getContentResolver().notifyChange(User.PhotoColumns.PHOTOS_URI, null);
                    return uri;
                }

                throw new IllegalStateException("could not insert " +
                        "content values: " + values);

            default:
                throw new IllegalArgumentException("Unrecognized Uri");
        }
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
