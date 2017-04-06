package lt.dualpair.android.data.resource;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public enum RelationshipStatus {

    @SerializedName("")
    NONE(""),

    @SerializedName("SI")
    SINGLE("SI"),

    @SerializedName("IR")
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
        return statusesByCode.get(code);
    }

}
