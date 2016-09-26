package lt.dualpair.android.data;

import android.database.sqlite.SQLiteDatabase;

public abstract class Repository<T> {

    protected SQLiteDatabase db;

    public Repository(SQLiteDatabase db) {
        this.db = db;
    }

    public T save(T t) {
        if (db.inTransaction()) {
            return doSave(t);
        } else {
            db.beginTransaction();
            try {
                T r = doSave(t);
                db.setTransactionSuccessful();
                return r;
            } finally {
                db.endTransaction();
            }
        }
    }

    protected abstract T doSave(T t);

    public void delete(T t) {
        if (db.inTransaction()) {
            doDelete(t);
        } else {
            db.beginTransaction();
            try {
                doDelete(t);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    protected abstract void doDelete(T t);

    protected String[] args(String... args) {
        return args;
    }

}
