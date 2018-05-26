package lt.dualpair.android.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.remote.client.device.RegisterDeviceClient;

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
            new RegisterDeviceClient(token).completable()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) {
                            if (e instanceof ServiceException) {
                                ServiceException se = (ServiceException)e;
                                if (se.getResponse().code() == 409) {
                                    Log.i(SERVICE_NAME, "Device already registered");
                                } else {
                                    Log.e(SERVICE_NAME, "Unable to register device", e);
                                }
                            }
                        }
                    });
        } catch (IOException ioe) {
            Log.e(SERVICE_NAME, "Unable to register device", ioe);
        }
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, RegistrationService.class);
    }
}
