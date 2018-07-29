package lt.dualpair.android.data.remote.resource;

import java.io.Serializable;
import java.util.Date;

public class Match implements Serializable {

    private Long id;
    private User user;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
