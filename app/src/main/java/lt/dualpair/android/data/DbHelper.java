package lt.dualpair.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lt.dualpair.android.R;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME_PREFIX = "DualPair_";
    private static final int VERSION = 36;

    private Context context;

    private DbHelper(Context context, String userId) {
        super(context, DB_NAME_PREFIX + userId, null, VERSION);
        this.context = context;
    }

    public static DbHelper forCurrentUser(Context context) {
        //String userId = AccountUtils.getUserId(context).toString(); // TODO check user;
        return new DbHelper(context, "1");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String schema = context.getResources().getString(R.string.create_db);
        for (String statement : schema.split(";")) {
            db.execSQL(statement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {        String schema = context.getResources().getString(R.string.create_db);
        for (String statement : schema.split(";")) {
            db.execSQL(statement);
        }
    }

    public static String getDateTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }
}
