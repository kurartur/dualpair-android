package lt.dualpair.android.data.remote.resource;

import java.io.Serializable;

public class Sociotype implements Serializable {

    private String code1;
    private String code2;

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }
}
