package lt.dualpair.android.bus;

public class NewMatchEvent extends Event {

    private Long userId;

    public NewMatchEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
