package lt.dualpair.android.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.ui.accounts.AccountType;

public class PhotoRepository extends Repository<Photo> {

    public PhotoRepository(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected Photo doSave(Photo photo) {
        return null;
    }

    @Override
    protected void doDelete(Photo photo) {
        db.delete(UserMeta.Photo.TABLE_NAME, "_id=?", new String[]{photo.getId().toString()});
    }

    public List<Photo> fetch(Long userId) {
        Cursor photosCursor = null;
        try {
            List<Photo> photos = new ArrayList<>();
            photosCursor = db.query(UserMeta.Photo.TABLE_NAME, null, "user_id=?", new String[]{userId.toString()}, null, null, null);
            while (photosCursor.moveToNext()) {
                Photo photo = new Photo();
                photo.setId(photosCursor.getLong(photosCursor.getColumnIndex(UserMeta.Photo._ID)));
                photo.setAccountType(AccountType.valueOf(photosCursor.getString(photosCursor.getColumnIndex(UserMeta.Photo.ACCOUNT_TYPE))));
                photo.setIdOnAccount(photosCursor.getString(photosCursor.getColumnIndex(UserMeta.Photo.ID_ON_ACCOUNT)));
                photo.setSourceUrl(photosCursor.getString(photosCursor.getColumnIndex(UserMeta.Photo.SOURCE_LINK)));
                photo.setPosition(photosCursor.getInt(photosCursor.getColumnIndex(UserMeta.Photo.POSITION)));
                photos.add(photo);
            }
            return photos;
        } finally {
            if (photosCursor != null) {
                photosCursor.close();
            }
        }
    }

    public Photo save(Photo photo, Long userId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserMeta.Photo._ID, photo.getId());
        contentValues.put(UserMeta.Photo.USER_ID, userId);
        contentValues.put(UserMeta.Photo.ACCOUNT_TYPE, photo.getAccountType().name());
        contentValues.put(UserMeta.Photo.ID_ON_ACCOUNT, photo.getIdOnAccount());
        contentValues.put(UserMeta.Photo.SOURCE_LINK, photo.getSourceUrl());
        contentValues.put(UserMeta.Photo.POSITION, photo.getPosition());
        long id = db.insert(UserMeta.Photo.TABLE_NAME, null, contentValues);
        assertOperation(id, "Unable to insert photo " + photo);
        return photo;
    }
}
