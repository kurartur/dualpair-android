package lt.dualpair.android.data.manager;

import android.content.Context;

public abstract class DataManager {

    protected Context context;

    public DataManager(Context context) {
        this.context = context;
    }
}
