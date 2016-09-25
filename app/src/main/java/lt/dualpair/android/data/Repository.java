package lt.dualpair.android.data;

import android.database.sqlite.SQLiteDatabase;

public abstract class Repository {

    protected SQLiteDatabase db;

    public Repository(SQLiteDatabase db) {
        this.db = db;
    }

}
