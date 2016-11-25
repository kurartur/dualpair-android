package lt.dualpair.android.data;

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
        DataManager.LinkedTask linkedTask = DataManager.getRequests().poll();
        if (linkedTask != null) {
            Observable observable = linkedTask.getTask().execute(this);
            observable.subscribe(linkedTask.getLink());
        }
    }
}
