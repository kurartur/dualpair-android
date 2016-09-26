package lt.dualpair.android.data.resource;

import java.util.List;

public class ResourceCollection<T> extends BaseResource {

    private List<T> content;
    private PageMeta page;

    public List<T> getContent() {
        return content;
    }

    public int getTotalElements() {
        return page.totalElements;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public static class PageMeta {

        private int size;
        private int totalElements;
        private int totalPages;
        private int number;

    }

}
