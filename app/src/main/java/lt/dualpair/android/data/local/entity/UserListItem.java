package lt.dualpair.android.data.local.entity;

public class UserListItem {

    private Long userId;
    private String name;
    private String photoSource;

    public UserListItem(Long userId, String name, String photoSource) {
        this.userId = userId;
        this.name = name;
        this.photoSource = photoSource;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhotoSource() {
        return photoSource;
    }

}
