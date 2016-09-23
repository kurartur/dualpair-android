package lt.dualpair.android.gcm.handler;

public interface MessageHandler<T> {

    void handleMessage(T payload);

    T convertPayload(String payload);

}
