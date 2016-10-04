package lt.dualpair.android.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.client.device.RegisterDeviceClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegistrationService extends IntentService {

    private static final String SERVICE_NAME = "RegistrationService";

    public RegistrationService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            new RegisterDeviceClient(token).observable()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new EmptySubscriber<Void>() {
                        @Override
                        public void onError(Throwable e) {
                            Log.e(SERVICE_NAME, "Unable to register device", e);
                        }

                        @Override
                        public void onNext(Void aVoid) {}
                    });
        } catch (IOException ioe) {
            Log.e(SERVICE_NAME, "Unable to register device", ioe);
        }
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, RegistrationService.class);
    }
}
