package lt.dualpair.android.data.resource;

import java.io.Serializable;

public class MatchParty implements Serializable {

    private Long id;
    private User user;
    private Response response;

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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
