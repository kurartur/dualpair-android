package lt.dualpair.android.data.resource;

import java.io.Serializable;

public class Photo implements Serializable {

    private String sourceUrl;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
