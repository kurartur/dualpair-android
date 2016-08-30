package lt.dualpair.android;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokenProvider.initialize(this);
    }
}
