package lt.dualpair.android.bus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private static final RxBus INSTANCE = new RxBus();

    private final Subject<Object> mBusSubject = PublishSubject.create();

    public static RxBus getInstance() {
        return INSTANCE;
    }

    public <T extends Event> Disposable register(final Class<T> eventClass, Consumer<T> onNext) {
        return mBusSubject
                .filter(event -> event.getClass().equals(eventClass))
                .map(o -> (T) o)
                .subscribe(onNext);
    }

    public <T extends Event> void post(T event) {
        mBusSubject.onNext(event);
    }
}
