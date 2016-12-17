package lt.dualpair.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.FragmentLifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseFragment extends Fragment implements FragmentLifecycleProvider {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @NonNull
    @Override
    public Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @NonNull
    @Override
    public <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        Log.d(getClass().getName(), "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        Log.d(getClass().getName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(getClass().getName(), "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
        Log.d(getClass().getName(), "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
        Log.d(getClass().getName(), "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        Log.d(getClass().getName(), "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycleSubject.onNext(FragmentEvent.STOP);
        Log.d(getClass().getName(), "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        Log.d(getClass().getName(), "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        Log.d(getClass().getName(), "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        Log.d(getClass().getName(), "onDetach");
    }

}
