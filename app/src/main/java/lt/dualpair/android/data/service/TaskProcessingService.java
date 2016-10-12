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
        DataManager.LinkedRequest linkedRequest = DataManager.getRequests().poll();
        if (linkedRequest != null) {
            Observable observable = Observable.fromCallable(linkedRequest.getDataRequest().getCreator().createTask(this));
            observable.subscribe(linkedRequest.getLink());
        }
    }
}
