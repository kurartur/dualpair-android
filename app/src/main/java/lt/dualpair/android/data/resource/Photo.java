package lt.dualpair.android.data.resource;

import java.io.Serializable;

public class Photo implements Serializable {

    private Long id;
    private String sourceUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
