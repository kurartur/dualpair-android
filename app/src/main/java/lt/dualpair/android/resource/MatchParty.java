package lt.dualpair.android.resource;

import java.io.Serializable;

public class MatchParty implements Serializable {

    private User user;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public User getUser() {
        return user;
    }
}
