package lt.dualpair.android.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import lt.dualpair.android.R;
import lt.dualpair.android.data.remote.client.ServiceException;

public class ErrorConverter {

    private Context context;

    public ErrorConverter(Fragment fragment) {
        context = fragment.getContext();
    }

    public String convert(Throwable throwable) {
        if (throwable instanceof ServiceException) {
            ServiceException se = (ServiceException)throwable;
            if (se.getKind() == ServiceException.Kind.NETWORK) {
                return context.getString(R.string.check_internet_connection);
            }
        }
        return throwable.getMessage();
    }

}
