
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

import edu.buffalo.cse.blue.pocketmocker.models.MockLocation;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;
import edu.buffalo.cse.blue.pocketmocker.models.MockScanResult;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEvent;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEventManager;
import edu.buffalo.cse.blue.pocketmocker.models.MockWifiManager;
import edu.buffalo.cse.blue.pocketmocker.models.RecordReplayManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MockerService extends Service {

    public static final String TAG = "MOCK_SRV";

    private final Messenger messenger = new Messenger(new IncomingHandler());
    private MockLocationManager mockLocationManager;
    private RecordReplayManager recordReplayManager;
    private MockSensorEventManager mockSensorEventManager;
    private MockWifiManager mockWifiManager;
    private MockLocation oldLoc;
    private MockLocation nextLoc;

    private final HashMap<String, Messenger> locationClients = new HashMap<String, Messenger>();
    private final HashMap<String, Messenger> sensorClients = new HashMap<String, Messenger>();
    private final HashMap<String, Messenger> wifiClients = new HashMap<String, Messenger>();

    @Override
    public void onCreate() {
        Log.v(TAG, "MockerService started.");
        mockLocationManager = MockLocationManager.getInstance(getApplicationContext());
        recordReplayManager = RecordReplayManager.getInstance(getApplicationContext());
        mockSensorEventManager = MockSensorEventManager.getInstance(getApplicationContext());
        mockWifiManager = MockWifiManager.getInstance(getApplicationContext());
        new Thread(new LocationReplayer()).start();
        new Thread(new SensorReplayer()).start();
        new Thread(new WifiReplayer()).start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return messenger.getBinder();
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "Message received: " + msg.toString());
            Bundle data = msg.getData();
            // This should be fine because even if one APK requests multiple
            // LocationListeners to the LocationManager, we don't care, we just
            // need to send their Context data.
            if (data.containsKey("package")) {
                String clientPackage = data.getString("package");
                Log.v(TAG, "Client: " + clientPackage);
                String[] packageParts = clientPackage.split("_");
                if (!packageParts[0].equals(getApplicationContext().getPackageName())) {
                    if (packageParts[1].equals("location")) {
                        locationClients.put(clientPackage, msg.replyTo);
                    } else if (packageParts[1].equals("sensor")) {
                        sensorClients.put(clientPackage, msg.replyTo);
                    } else if (packageParts[1].equals("wifi")) {
                        Log.v(TAG, "wifi client: " + clientPackage);
                        wifiClients.put(clientPackage, msg.replyTo);
                    }
                }
            }
        }
    }

    private class WifiReplayer implements Runnable {

        private void sendMockScanResults(ArrayList<MockScanResult> result, Messenger m) {
            Bundle data = new Bundle();
            data.putInt("size", result.size());
            data.putBoolean("isReplaying", true);
            for (int i = 0; i < result.size(); i++) {
                data.putBundle(String.valueOf(i), result.get(i)
                        .toBundle(System.currentTimeMillis()));
            }
            Message mockMsg = Message.obtain();
            mockMsg.setData(data);
            try {
                m.send(mockMsg);
            } catch (RemoteException e) {
                Log.v(Tag, "Having a hard time sending msg", e);
                e.printStackTrace();
            }
        }

        private void sendTermination(Messenger m) {
            Bundle data = new Bundle();
            data.putBoolean("isReplaying", false);
            Message mockMsg = Message.obtain();
            mockMsg.setData(data);
            try {
                m.send(mockMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(ArrayList<MockScanResult> scanResults) {
            for (String client : wifiClients.keySet()) {
                if (scanResults != null) {
                    sendMockScanResults(scanResults, wifiClients.get(client));
                    Log.v(TAG, "Sending mock scan results: " + scanResults.toString());
                } else {
                    sendTermination(wifiClients.get(client));
                    Log.v(TAG, "Sending terminatino!");
                }
            }
        }

        private String Tag = TAG + "_wifi";

        @Override
        public void run() {
            long[] groupStartTimestamps = mockWifiManager.getScanResultGroupsForCurrentRecording();
            ArrayList<MockScanResult> scanResults;
            long timeDelta;
            while (true) {
                Log.v(Tag, "Group timestamps: " + groupStartTimestamps.length);
                for (int i = 0; i < groupStartTimestamps.length; i++) {
                    if (!recordReplayManager.isReplaying()) {
                        // If we aren't replaying, break this loop early and
                        // send a termination broadcast
                        break;
                    }
                    scanResults = mockWifiManager.getCurrentRecordingScanResultsForGroup(i);
                    Log.v(Tag, "Sending results: " + scanResults.toString());
                    broadcast(scanResults);
                    if (i != groupStartTimestamps.length - 1) {
                        timeDelta = groupStartTimestamps[i + 1] - groupStartTimestamps[i];
                        Log.v(Tag, "Wifi wait: " + timeDelta);
                        try {
                            Thread.sleep(timeDelta);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.v(Tag, "Usually we would stop replaying wifi around now....");
                // broadcast(null);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                data.putBoolean("isReplaying", true);
                id = mLoc.getId();
                data.putLong("mockId", id);
            } else {
                // In the case that we are not replaying.
                Log.v(TAG, "We do not have a location to mock!");
                data = new Bundle();
                data.putBoolean("hasLocation", false);
                data.putBoolean("isReplaying", false);
                data.putLong("mockId", id);
            }
            Message reply = Message.obtain();
            reply.setData(data);
            try {
                Log.v(TAG, "Sending location for mock: " + id);
                m.send(reply);
            } catch (RemoteException e) {
                // TODO: Handle android.os.DeadObjectException better.
                Log.v(TAG, "RemoteException: " + e.toString());
            }
        }
    }

    private class LocationReplayer implements Runnable {

        private boolean hasNotifiedStop = false;

        private void broadcastMockLocation(MockLocation m) {
            for (String clientPkg : locationClients.keySet()) {
                Log.v(TAG, "Sending message to: " + clientPkg);
                new MockLocationSender(locationClients.get(clientPkg), m).start();
            }
        }

        @Override
        public void run() {
            Log.v(TAG, "Starting LocationReplayer");
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
                        if (nextLoc == null) {
                            // If the user stops replaying early, we should be
                            // able to handle that event.
                            // Just being safe here.
                            Log.v(TAG, "Stopped recording early.");
                            broadcastMockLocation(null);
                            hasNotifiedStop = true;
                            recordReplayManager.setIsReplaying(false);
                            mockLocationManager.kill();
                            Log.v(TAG,
                                    "We should go around again and stop because mockLocations hasNext="
                                            + mockLocationManager.hasNext());
                        }
                        else {
                            Log.v(TAG, "Next loc: " + nextLoc.getId());
                            timeToWait = nextLoc.getRealLocation().getTime()
                                    - oldLoc.getRealLocation().getTime();
                            broadcastMockLocation(oldLoc);
                            try {
                                Log.v(TAG, "Sleeping for " + timeToWait);
                                // timeToWait is seconds
                                // Catch rollover seconds because it turns out
                                // negative at the end and I don't feel like
                                // actually fixing that right now
                                if (timeToWait > 0) {
                                    Thread.sleep(timeToWait * 1000);
                                }
                            } catch (InterruptedException e) {
                                Log.v(TAG, "InterruptedException:", e);
                            }
                        }
                        if (recordReplayManager.isReplaying()) {
                            // By this point oldLoc has been sent, but nextLoc
                            // is
                            // our
                            // last
                            // location to send.
                            broadcastMockLocation(nextLoc);
                            Log.v(TAG, "No more mocked locations! Old loc: " + oldLoc.getId()
                                    + " Next loc: "
                                    + nextLoc.getId());
                        }
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

    private class SensorReplayer implements Runnable {

        private boolean hasNotifiedStop = false;
        private MockSensorEvent oldEvent;
        private MockSensorEvent newEvent;

        private void sendMockSensorEvent(MockSensorEvent e, Messenger m) {
            Bundle data;
            long id = -1;
            if (e != null) {
                Log.v(TAG, "We have a sensor event to mock.");
                data = e.toBundle(System.currentTimeMillis());
                data.putBoolean("isReplaying", true);
                id = e.getId();
            } else {
                Log.v(TAG, "We do not have a sensor event to mock!");
                data = new Bundle();
                data.putBoolean("isReplaying", false);
            }
            data.putLong("mockId", id);
            Message mockMessage = Message.obtain();
            mockMessage.setData(data);
            try {
                m.send(mockMessage);
            } catch (RemoteException e1) {
                // TODO: Handle android.os.DeadObjectException better.
                e1.printStackTrace();
            }
        }

        private void broadcastMockSensorEvent(MockSensorEvent e) {
            for (String client : sensorClients.keySet()) {
                Log.v(TAG, "Sending mock sensor event to: " + client);
                sendMockSensorEvent(e, sensorClients.get(client));
            }
        }

        @Override
        public void run() {
            Log.v(TAG, "Starting SensorReplayer");
            long timeToWait;
            while (true) {
                if (recordReplayManager.isReplaying()) {
                    if (!mockSensorEventManager.isReady()) {
                        mockSensorEventManager.init();
                    }
                    while (mockSensorEventManager.hasNext()) {
                        if (!recordReplayManager.isReplaying()) {
                            Log.v(TAG, "Stopped recording early.");
                            broadcastMockSensorEvent(null);
                            hasNotifiedStop = true;
                            break;
                        }
                        hasNotifiedStop = false;
                        if (oldEvent == null) {
                            oldEvent = mockSensorEventManager.getNext();
                            Log.v(TAG, "old event: " + oldEvent.getId());
                        } else {
                            oldEvent = newEvent;
                            Log.v(TAG, "old event: " + oldEvent.getId());
                        }
                        newEvent = mockSensorEventManager.getNext();
                        Log.v(TAG, "new event: " + newEvent.getId());
                        timeToWait = newEvent.getEventTimestamp() - oldEvent.getEventTimestamp();
                        broadcastMockSensorEvent(oldEvent);
                        Log.v(TAG, "Sensor thread sleeping for: " + timeToWait);
                        if (timeToWait > 0) {
                            try {
                                Thread.sleep(timeToWait);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (recordReplayManager.isReplaying()) {
                        broadcastMockSensorEvent(newEvent);
                        Log.v(TAG, "No more sensor events!");
                    }
                } else {
                    if (!hasNotifiedStop) {
                        Log.v(TAG, "Sensor thread notifying stop");
                        broadcastMockSensorEvent(null);
                        hasNotifiedStop = true;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
