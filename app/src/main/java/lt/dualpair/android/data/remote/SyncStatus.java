package lt.dualpair.android.data.remote;


public enum SyncStatus {

    INSERT("C"),
    READY("R"),
    UPDATE("U"),
    DELETE("D");

    private String code;

    SyncStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
