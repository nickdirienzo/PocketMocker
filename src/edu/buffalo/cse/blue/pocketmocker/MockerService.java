
package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.service.LocationReplayer;
import edu.buffalo.cse.blue.pocketmocker.service.SensorReplayer;
import edu.buffalo.cse.blue.pocketmocker.service.WifiReplayer;

import java.util.HashMap;

public class MockerService extends Service {

    public static final String TAG = "MOCK_SRV";

    public static final String PM_ACTION_KEY = "pm_act";
    public static final int PM_ACTION_SUB = 0;
    public static final int PM_ACTION_START_REPLAY = 1;
    public static final int PM_ACTION_STOP_REPLAY = 2;
    public static final String PACKAGE = "package";
    public static final String PACKAGE_SUFFIX_DELIM = "_";
    public static final String LOCATION_SUFFIX = "location";
    public static final String SENSOR_SUFFIX = "sensor";
    public static final String WIFI_SUFFIX = "wifi";
    public static final String IS_REPLAYING = "isReplaying";

    private final Messenger messenger = new Messenger(new SubscriberHandler(this));

    private final HashMap<String, Messenger> locationClients = new HashMap<String, Messenger>();
    private final HashMap<String, Messenger> sensorClients = new HashMap<String, Messenger>();
    private final HashMap<String, Messenger> wifiClients = new HashMap<String, Messenger>();

    private Messenger mActivityMessenger;
    private Thread mLocationReplayThread;
    private Thread mSensorReplayThread;
    private Thread mWifiReplayThread;

    @Override
    public void onCreate() {
        Log.v(TAG, "MockerService started.");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return messenger.getBinder();
    }

    public HashMap<String, Messenger> getLocationClients() {
        return locationClients;
    }

    public HashMap<String, Messenger> getSensorClients() {
        return sensorClients;
    }

    public HashMap<String, Messenger> getWifiClients() {
        return wifiClients;
    }

    /**
     * Called by our replay threads before they die. Once all are dead, we send
     * a termination message to all of our clients to signal the end of the
     * replay session.
     */
    public void signalDeathAndMaybeBroadcastTermination() {
        if (!mLocationReplayThread.isAlive() && !mSensorReplayThread.isAlive()
                && !mWifiReplayThread.isAlive()) {
            broadcastTermination();
        }
    }

    private void broadcastMessage(Message msg) {
        for (Messenger m : locationClients.values()) {
            try {
                m.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG, "One of our location clients is dead and gone. Oh well.");
            }
        }
        for (Messenger m : sensorClients.values()) {
            try {
                m.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG, "One of our sensor clients is dead and gone. Oh well.");
            }
        }
        for (Messenger m : wifiClients.values()) {
            try {
                m.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG, "One of our wifi clients is dead and gone. Oh well.");
            }
        }
        try {
            mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            Log.w(TAG, "Out activity messenger is dead and gone. Oh well.");
        }
    }

    private void broadcastTermination() {
        Bundle data = new Bundle();
        data.putBoolean(IS_REPLAYING, false);
        Message termMsg = Message.obtain();
        termMsg.setData(data);
        broadcastMessage(termMsg);
    }

    @SuppressLint("HandlerLeak")
    class SubscriberHandler extends Handler {

        private MockerService mMockerService;

        public SubscriberHandler(MockerService m) {
            mMockerService = m;
        }

        private boolean validActivityObserver(Bundle data) {
            if (data.containsKey(PM_ACTION_KEY) && data.containsKey(PACKAGE)) {
                if (data.getString(PACKAGE).equals(getApplicationContext().getPackageName())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            // Initiate pub-sub between PM Activity and the Service so we don't
            // have to busy way
            if (validActivityObserver(data)) {
                // The initiating connection will contain a replyTo. All other
                // commands will not.
                switch (data.getInt(PM_ACTION_KEY)) {
                    case PM_ACTION_SUB:
                        mActivityMessenger = msg.replyTo;
                        break;
                    case PM_ACTION_START_REPLAY:
                        mLocationReplayThread = new Thread(new LocationReplayer(mMockerService));
                        mSensorReplayThread = new Thread(new SensorReplayer(mMockerService));
                        mWifiReplayThread = new Thread(new WifiReplayer(mMockerService));
                        mLocationReplayThread.start();
                        mSensorReplayThread.start();
                        mWifiReplayThread.start();
                        break;
                    case PM_ACTION_STOP_REPLAY:
                        // User prematurely stops replaying
                        mLocationReplayThread.interrupt();
                        mSensorReplayThread.interrupt();
                        mWifiReplayThread.interrupt();
                        break;
                }
            } else { // Assume it's a platform subscriber
                if (data.containsKey(PACKAGE)) {
                    String clientPackage = data.getString(PACKAGE);
                    Log.v(TAG, "Client: " + clientPackage);
                    String[] packageParts = clientPackage.split(PACKAGE_SUFFIX_DELIM);
                    if (!packageParts[0].equals(getApplicationContext().getPackageName())) {
                        if (packageParts[1].equals(LOCATION_SUFFIX)) {
                            locationClients.put(clientPackage, msg.replyTo);
                        } else if (packageParts[1].equals(SENSOR_SUFFIX)) {
                            sensorClients.put(clientPackage, msg.replyTo);
                        } else if (packageParts[1].equals(WIFI_SUFFIX)) {
                            Log.v(TAG, "wifi client: " + clientPackage);
                            wifiClients.put(clientPackage, msg.replyTo);
                        }
                    }
                }
            }
        }
    }

}
