package lt.dualpair.android.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

public class CustomInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh(); // TODO save device
    }

}
