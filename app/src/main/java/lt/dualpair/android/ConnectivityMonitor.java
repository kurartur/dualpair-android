package lt.dualpair.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class ConnectivityMonitor {

    private static ConnectivityMonitor instance;

    private BehaviorSubject<ConnectivityInfo> connectivityInfoSubject = BehaviorSubject.create();

    private ConnectivityMonitor(Context context) {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkConnectivity(context);
            }
        }, intentFilter);
        checkConnectivity(context);
    }

    public static void initialize(Context context) {
        instance = new ConnectivityMonitor(context);
    }

    public static ConnectivityMonitor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized");
        }
        return instance;
    }

    public Observable<ConnectivityInfo> getConnectivityInfo() {
        return connectivityInfoSubject;
    }

    private void checkConnectivity(Context context) {
        connectivityInfoSubject.onNext(new ConnectivityInfo(isNetworkAvailable(context)));
    }

    private boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        return false;
    }

    public static class ConnectivityInfo {

        private boolean networkAvailable;

        public ConnectivityInfo(boolean networkAvailable) {
            this.networkAvailable = networkAvailable;
        }

        public boolean isNetworkAvailable() {
            return networkAvailable;
        }
    }

}
