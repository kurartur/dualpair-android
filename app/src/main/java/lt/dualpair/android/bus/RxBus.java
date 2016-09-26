package lt.dualpair.android.bus;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private static final RxBus INSTANCE = new RxBus();

    private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

    public static RxBus getInstance() {
        return INSTANCE;
    }

    public <T extends Event> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
        return mBusSubject
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object event) {
                        return event.getClass().equals(eventClass);
                    }
                })
                .map(new Func1<Object, T>() {
                    @Override
                    public T call(Object o) {
                        return (T) o;
                    }
                })
                .subscribe(onNext);
    }

    public <T extends Event> void post(T event) {
        mBusSubject.onNext(event);
    }
}
