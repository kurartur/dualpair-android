package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import lt.dualpair.android.data.service.TaskProcessingService;
import lt.dualpair.android.data.task.Task;
import rx.Observer;

public abstract class DataManager {

    protected Context context;

    private static Queue<QueuedTask> tasks = new ConcurrentLinkedQueue<>();

    public DataManager(Context context) {
        this.context = context;
    }

    protected void enqueueTask(QueuedTask queuedTask) {
        tasks.add(queuedTask);
        Intent intent = new Intent(context, TaskProcessingService.class);
        context.startService(intent);
    }

    public static Queue<QueuedTask> getTasks() {
        return tasks;
    }

    public static class QueuedTask<T> {

        private String key;
        private TaskCreator<T> creator;
        private Observer<T> observer;

        public QueuedTask(String key, TaskCreator<T> creator, Observer<T> subscriber) {
            this.key = key;
            this.creator = creator;
            this.observer = subscriber;
        }

        public String getKey() {
            return key;
        }

        public TaskCreator<T> getCreator() {
            return creator;
        }

        public Observer<T> getObserver() {
            return observer;
        }
    }

    public interface TaskCreator<T> {
        Task<T> createTask(Context context);
    }
}
