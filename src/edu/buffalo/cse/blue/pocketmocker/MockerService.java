
package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.models.MockLocation;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;
import edu.buffalo.cse.blue.pocketmocker.models.RecordReplayManager;

import java.util.HashMap;

public class MockerService extends Service {

    public static final String TAG = "MOCK_SRV";

    private final Messenger messenger = new Messenger(new IncomingHandler());
    private MockLocationManager mockLocationManager;
    private RecordReplayManager recordReplayManager;
    private MockLocation oldLoc;
    private MockLocation nextLoc;

    private final HashMap<String, Messenger> clients = new HashMap<String, Messenger>();

    @Override
    public void onCreate() {
        Log.v(TAG, "MockerService started.");
        mockLocationManager = MockLocationManager.getInstance(getApplicationContext());
        recordReplayManager = RecordReplayManager.getInstance(getApplicationContext());
        new Thread(new ReplayMonitor()).start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return messenger.getBinder();
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        // TODO: Handle messages in their own thread
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "Message received: " + msg.toString());
            Bundle data = msg.getData();
            // This should be fine because even if one APK requests multiple
            // LocationListeners to the LocationManager, we don't care, we just
            // need to send their Context data.
            String clientPackage = data.getString("package");
            if (!clients.containsKey(clientPackage)
                    // We cannot subscribe to ourself
                    && !clientPackage.equals(getApplicationContext().getPackageName())) {
                Log.v(TAG, "Adding sender for " + clientPackage + " to clients.");
                clients.put(clientPackage, msg.replyTo);
                Log.v(TAG, "Clients: " + clients.keySet().toString());
            }
        }
    }

    private void broadcastMockLocation(MockLocation m) {
        for (String clientPkg : clients.keySet()) {
            Log.v(TAG, "Sending message to: " + clientPkg);
            new MockLocationSender(clients.get(clientPkg), m).start();
        }
    }

    private class MockLocationSender extends Thread {

        private Messenger messenger;
        private MockLocation mockLocation;

        public MockLocationSender(Messenger m, MockLocation mLoc) {
            messenger = m;
            mockLocation = mLoc;
        }

        @Override
        public void run() {
            Log.v(TAG, "Running thread!");
            sendMockLocation(mockLocation, messenger);
            Log.v(TAG, "Done running thread.");
        }

        /**
         * Guarantees the following keys: bool hasLocation, long mockId. If
         * hasLocation is true, then we are also replaying. If hasLocation is
         * false, we are not replaying.
         * 
         * @param mLoc
         * @param m
         * @param isReplaying
         */
        private void sendMockLocation(MockLocation mLoc, Messenger m) {
            Bundle data;
            long id = -1;
            if (mLoc != null) {
                Log.v(TAG, "We have a location to mock!");
                data = mLoc.toBundle(System.currentTimeMillis());
                data.putBoolean("hasLocation", true);
                id = mLoc.getId();
                data.putLong("mockId", id);
            } else {
                // In the case that we are not replaying.
                Log.v(TAG, "We do not have a location to mock!");
                data = new Bundle();
                data.putBoolean("hasLocation", false);
                data.putLong("mockId", id);
            }
            Message reply = Message.obtain();
            reply.setData(data);
            try {
                Log.v(TAG, "Sending location for mock: " + id);
                m.send(reply);
            } catch (RemoteException e) {
                Log.v(TAG, "RemoteException: " + e.toString());
            }
        }
    }

    private class ReplayMonitor implements Runnable {
        
        private boolean hasNotifiedStop = false;

        @Override
        public void run() {
            long timeToWait;
            // For "security" purposes, we poll the DB to determine if we should
            // be replaying or not. Otherwise, another application could send a
            // message to the MockerService to disable replaying.
            while (true) {
                if (recordReplayManager.isReplaying()) {
                    Log.v(TAG, "We are now replaying!");
                    // Stupid hack for now
                    if (!mockLocationManager.isReady()) {
                        mockLocationManager.init();
                    }
                    while (mockLocationManager.hasNext()) {
                        if (!recordReplayManager.isReplaying()) {
                            // If the user stops replaying early, we should be
                            // able to handle that event.
                            // Just being safe here.
                            Log.v(TAG, "Stopped recording early.");
                            broadcastMockLocation(null);
                            hasNotifiedStop = true;
                            break;
                        }
                        hasNotifiedStop = false;
                        if (oldLoc == null) {
                            oldLoc = mockLocationManager.getNext();
                            Log.v(TAG, "Old loc: " + oldLoc.getId());
                        } else {
                            oldLoc = nextLoc;
                            Log.v(TAG, "Old loc: " + oldLoc.getId());
                        }
                        nextLoc = mockLocationManager.getNext();
                        Log.v(TAG, "Next loc: " + nextLoc.getId());
                        timeToWait = nextLoc.getRealLocation().getTime()
                                - oldLoc.getRealLocation().getTime();
                        broadcastMockLocation(oldLoc);
                        try {
                            Log.v(TAG, "Sleeping for " + timeToWait);
                            // timeToWait is seconds
                            Thread.sleep(timeToWait * 1000);
                        } catch (InterruptedException e) {
                            Log.v(TAG, "InterruptedException:", e);
                        }
                    }
                    if (recordReplayManager.isReplaying()) {
                        // By this point oldLoc has been sent, but nextLoc is
                        // our
                        // last
                        // location to send.
                        broadcastMockLocation(nextLoc);
                        Log.v(TAG, "No more mocked locations! Old loc: " + oldLoc.getId()
                                + " Next loc: "
                                + nextLoc.getId());
                    }
                } else {
                    if (!hasNotifiedStop) {
                        Log.v(TAG, "Notifying about stop.");
                        broadcastMockLocation(null);
                        hasNotifiedStop = true;
                    }

                }
                try {
                    // We shouldn't hammer the CPU, so it's okay to be a second
                    // behind. We can lower this bound if need be.
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.v(TAG, "InterruptedException:", e);
                }
            }
        }
    }
}
