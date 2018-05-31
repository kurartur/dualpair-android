package lt.dualpair.android;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKSdk;

import lt.dualpair.android.data.remote.client.TokenProvider;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // vkontakte
        VKSdk.initialize(this);

        TokenProvider.initialize(this);
    }
}
