package lt.dualpair.android.gcm.handler;

import android.content.Context;

import lt.dualpair.android.gcm.MessageType;

public class MessageHandlerFactory {

    private Context context;

    public MessageHandlerFactory(Context context) {
        this.context = context;
    }

    public MessageHandler getHandlerForType(MessageType type) {
        if (type == MessageType.NEW_MATCH) {
            return new NewMatchMessageHandler(context);
        }
        throw new IllegalArgumentException("Handler for type " + type + " not found");
    }

}
