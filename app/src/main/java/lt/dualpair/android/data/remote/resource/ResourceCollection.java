package lt.dualpair.android.data.remote.resource;

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

    public PageMeta getPageMeta() {
        return page;
    }

    public static class PageMeta {

        private int size;
        private int totalElements;
        private int totalPages;
        private int number;

        public int getSize() {
            return size;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getNumber() {
            return number;
        }
    }

}
