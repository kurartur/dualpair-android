package lt.dualpair.android.resource;

import java.io.Serializable;

public class MatchParty implements Serializable {

    private Long id;
    private User user;
    private Response response;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Response getResponse() {
        return response;
    }
}
