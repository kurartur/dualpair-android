package lt.dualpair.android.data.service;

import android.app.IntentService;
import android.content.Intent;

import lt.dualpair.android.data.manager.DataManager;
import rx.Observable;


public class TaskProcessingService extends IntentService {

    public TaskProcessingService() {
        super("TaskProcessingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DataManager.QueuedTask queuedTask = DataManager.getTasks().poll();
        if (queuedTask != null) {
            Observable observable = Observable.fromCallable(queuedTask.getCreator().createTask(this));
            observable.subscribe(queuedTask.getObserver());
        }
    }
}
