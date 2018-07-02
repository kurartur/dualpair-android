package lt.dualpair.android.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import io.reactivex.functions.Consumer;
import lt.dualpair.android.utils.ToastUtils;

public class UserFriendlyErrorConsumer implements Consumer<Throwable> {

    private Context context;
    private ErrorConverter converter;
    private Consumer<Throwable> consumer;

    public UserFriendlyErrorConsumer(Fragment fragment) {
        context = fragment.getContext();
        converter = new ErrorConverter(fragment);
    }

    public UserFriendlyErrorConsumer(Fragment fragment, Consumer<Throwable> consumer) {
        this(fragment);
        this.consumer = consumer;
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.e(context.getClass().getName(), throwable.getMessage(), throwable);
        ToastUtils.show(context, converter.convert(throwable));
        if (consumer != null) {
            consumer.accept(throwable);
        }
    }

}
