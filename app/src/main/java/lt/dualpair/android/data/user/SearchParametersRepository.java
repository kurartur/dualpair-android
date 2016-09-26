package lt.dualpair.android.data.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.Repository;
import lt.dualpair.android.data.resource.SearchParameters;

public class SearchParametersRepository extends Repository<SearchParameters> {

    public SearchParametersRepository(SQLiteDatabase db) {
        super(db);
    }

    public SearchParameters get() {
        Cursor c = db.query("search_parameters", null, null, null, null, null, "_id desc");
        if (c.moveToNext()) {
            return map(c);
        } else {
            return null;
        }
    }

    private SearchParameters map(Cursor c) {
        SearchParameters sp = new SearchParameters();
        sp.setMinAge(c.getInt(c.getColumnIndex("min_age")));
        sp.setMaxAge(c.getInt(c.getColumnIndex("max_age")));

        String searchFemale = c.getString(c.getColumnIndex("search_female"));
        sp.setSearchFemale("Y".equals(searchFemale) ? true : false);

        String searchMale = c.getString(c.getColumnIndex("search_male"));
        sp.setSearchMale("Y".equals(searchMale) ? true : false);
        
        return sp;
    }

    @Override
    protected SearchParameters doSave(SearchParameters sp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("min_age", sp.getMinAge());
        contentValues.put("max_age", sp.getMaxAge());
        contentValues.put("search_female", sp.getSearchFemale() ? "Y" : "N");
        contentValues.put("search_male", sp.getSearchMale() ? "Y" : "N");
        long rowId = db.insert("search_parameters", null, contentValues);
        return sp;
    }

    @Override
    protected void doDelete(SearchParameters searchParameters) {
        db.delete(SearchParametersMeta.SearchParameters.TABLE_NAME, null, null);
    }
}
