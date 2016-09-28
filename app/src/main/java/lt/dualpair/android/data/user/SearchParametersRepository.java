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

    public SearchParameters getLastUsed() {
        Cursor c = db.query(SearchParametersMeta.SearchParameters.TABLE_NAME, null, null, null, null, null, "_id desc");
        if (c.moveToNext()) {
            return map(c);
        } else {
            return null;
        }
    }

    private SearchParameters map(Cursor c) {
        SearchParameters sp = new SearchParameters();
        sp.setMinAge(c.getInt(c.getColumnIndex(SearchParametersMeta.SearchParameters.MIN_AGE)));
        sp.setMaxAge(c.getInt(c.getColumnIndex(SearchParametersMeta.SearchParameters.MAX_AGE)));

        String searchFemale = c.getString(c.getColumnIndex(SearchParametersMeta.SearchParameters.SEARCH_FEMALE));
        sp.setSearchFemale("Y".equals(searchFemale));

        String searchMale = c.getString(c.getColumnIndex(SearchParametersMeta.SearchParameters.SEARCH_MALE));
        sp.setSearchMale("Y".equals(searchMale));
        
        return sp;
    }

    @Override
    protected SearchParameters doSave(SearchParameters sp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SearchParametersMeta.SearchParameters.MIN_AGE, sp.getMinAge());
        contentValues.put(SearchParametersMeta.SearchParameters.MAX_AGE, sp.getMaxAge());
        contentValues.put(SearchParametersMeta.SearchParameters.SEARCH_FEMALE, sp.getSearchFemale() ? "Y" : "N");
        contentValues.put(SearchParametersMeta.SearchParameters.SEARCH_MALE, sp.getSearchMale() ? "Y" : "N");
        long rowId = db.insert(SearchParametersMeta.SearchParameters.TABLE_NAME, null, contentValues);
        return sp;
    }

    @Override
    protected void doDelete(SearchParameters searchParameters) {
        db.delete(SearchParametersMeta.SearchParameters.TABLE_NAME, null, null);
    }
}
