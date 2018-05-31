package lt.dualpair.android.ui;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public abstract class ResourceObserver<T> implements Observer<Resource<T>> {

    @Override
    public void onChanged(@Nullable Resource<T> resource) {
        if (resource != null) {
            if (resource.isLoading()) {
                onLoading();
            } else if (resource.isSuccess()) {
                onSuccess(resource.getData());
            } else if (resource.isError()) {
                onError(resource.getError());
            } else {
                throw new IllegalStateException("Unknown status");
            }
        } else {
            throw new NullPointerException("Resource expected");
        }
    }

    public abstract void onLoading();

    public abstract void onError(Throwable throwable);

    public abstract void onSuccess(T data);

}
