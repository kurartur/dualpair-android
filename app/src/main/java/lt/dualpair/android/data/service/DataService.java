package lt.dualpair.android.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import lt.dualpair.android.data.manager.DataManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class DataService extends Service {

    private static volatile boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            attemptTasks();
        }
        return START_STICKY;
    }

    private void attemptTasks() {
        DataManager.QueuedTask queuedTask = DataManager.getTasks().poll();
        if (queuedTask != null) {
            runTask(queuedTask);
        } else {
            isRunning = false;
            stopSelf();
        }
    }

    private void runTask(DataManager.QueuedTask queuedTask) {
        Observable observable = Observable.fromCallable(queuedTask.getTask());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        attemptTasks();
                    }
                }).subscribe(queuedTask.getSubscriber());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
