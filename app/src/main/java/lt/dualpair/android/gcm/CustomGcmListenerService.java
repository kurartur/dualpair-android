package lt.dualpair.android.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import lt.dualpair.android.gcm.handler.NewMatchMessageHandler;

public class CustomGcmListenerService extends GcmListenerService {

    private static final String TAG = "GcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Long userId = Long.valueOf(getAndFailIfNotExists(data, "to"));
        MessageType type = MessageType.valueOf(getAndFailIfNotExists(data, "type"));
        String payload = getAndFailIfNotExists(data, "payload");
        logEvent(from, userId, type, payload);
        handleMessage(userId, type, payload);
    }

    private String getAndFailIfNotExists(Bundle data, String key) {
        if (!data.containsKey(key))
            throw new IllegalStateException("Bundle does not contain key " + key);
        return data.getString(key);
    }

    private void logEvent(String from, Long userId, MessageType type, String payload) {
        String message = "Message received: " +
                "from=" + from + ", " +
                "to=" + userId + ", " +
                "type=" + type + ", " +
                "payload=" + payload;
        Log.d(TAG, message);
    }

    private void handleMessage(Long userId, MessageType type, String payload) {
        if (type == MessageType.NEW_MATCH) {
            NewMatchMessageHandler handler = new NewMatchMessageHandler(this);
            handler.handleMessage(handler.convertPayload(payload));
        }
    }
}
