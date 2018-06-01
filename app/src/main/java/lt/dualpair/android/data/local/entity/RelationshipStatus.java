package lt.dualpair.android.data.local.entity;

import java.util.HashMap;
import java.util.Map;

public enum RelationshipStatus {

    NONE(""),
    SINGLE("SI"),
    IN_RELATIONSHIP("IR");

    private String code;
    private static Map<String, RelationshipStatus> statusesByCode = new HashMap<>();

    static {
        for (RelationshipStatus status : RelationshipStatus.values()) {
            statusesByCode.put(status.code, status);
        }
    }

    RelationshipStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RelationshipStatus fromCode(String code) {
        if (code == null) {
            return NONE;
        }
        return statusesByCode.get(code);
    }

}
