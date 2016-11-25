package lt.dualpair.android.data.manager;

public class DataRequest<T> {

    private String key;
    private TaskCreator<T> creator;

    public DataRequest(String key, TaskCreator<T> creator) {
        this.key = key;
        this.creator = creator;
    }

    public String getKey() {
        return key;
    }

    public TaskCreator<T> getCreator() {
        return creator;
    }
}

