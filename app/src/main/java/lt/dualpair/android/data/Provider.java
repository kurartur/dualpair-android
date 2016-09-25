package lt.dualpair.android.data;

import android.content.Context;

public abstract class Provider {

    protected Context context;

    public Provider(Context context) {
        this.context = context;
    }
}
