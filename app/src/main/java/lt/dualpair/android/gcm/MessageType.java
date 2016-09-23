package lt.dualpair.android.gcm;

import java.util.Map;

public enum MessageType {

    NEW_MATCH(Map.class);

    private Class dataType;

    MessageType(Class dataType) {
        this.dataType =  dataType;
    }

    public Class getDataType() {
        return dataType;
    }
}
