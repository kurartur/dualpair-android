package lt.dualpair.android.data.resource;

import java.util.Collection;

public abstract class BaseResource {

    private Collection<Link> links;

    public Collection<Link> getLinks() {
        return links;
    }

    public Link getLink(String rel) {
        for (Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }
}
