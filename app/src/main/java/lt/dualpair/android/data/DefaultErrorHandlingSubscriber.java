package lt.dualpair.android.data;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import lt.dualpair.android.data.remote.services.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.utils.ToastUtils;

public class DefaultErrorHandlingSubscriber<T> extends EmptySubscriber<T> {

    private Context context;
    private String logTag;

    public DefaultErrorHandlingSubscriber(Context context) {
        this.context = context;
        this.logTag = context.getClass().getName();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException)e;
            try {
                ErrorResponse errorResponse = se.getErrorBodyAs(ErrorResponse.class);
                ToastUtils.show(context, errorResponse.getMessage());
            } catch (IOException ioe) {
                Log.e(logTag, "Error converting to error response", e);
            }
        } else {
            Log.e(logTag, "Error", e);
        }
    }
}
