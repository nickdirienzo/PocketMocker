
package edu.buffalo.cse.blue.pocketmocker.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MockerService;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEvent;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEventManager;

import java.util.ArrayList;

public class SensorReplayer implements Runnable {

    private static final String TAG = MockerService.TAG + MockerService.PACKAGE_SUFFIX_DELIM
            + MockerService.SENSOR_SUFFIX;

    private MockerService mMockerService;
    private MockSensorEventManager mMockSensorEventManager;

    public SensorReplayer(MockerService m) {
        mMockerService = m;
        mMockSensorEventManager = MockSensorEventManager.getInstance(m.getApplicationContext());
    }

    private void sendMockSensorEvent(MockSensorEvent e, Messenger m) {
        Bundle data = new Bundle();
        Log.v(TAG, "We have a sensor event to mock.");
        data = e.toBundle(System.currentTimeMillis());
        data.putBoolean("isReplaying", true);
        long id = e.getId();
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
        for (String client : mMockerService.getSensorClients().keySet()) {
            Log.v(TAG, "Sending mock sensor event to: " + client);
            sendMockSensorEvent(e, mMockerService.getSensorClients().get(client));
        }
    }

    @Override
    public void run() {
        // Returned in chronological order, so i+1 always happens after i
        ArrayList<MockSensorEvent> sensorEvents = mMockSensorEventManager
                .getMockSensorEventsForCurrentRecording();
        long timeDelta = 0;
        for (int i = 0; i < sensorEvents.size(); i++) {
            broadcastMockSensorEvent(sensorEvents.get(i));
            if (i != sensorEvents.size() - 1) {
                timeDelta = sensorEvents.get(i + 1).getEventTimestamp()
                        - sensorEvents.get(i).getEventTimestamp();
                Log.v(TAG, "Waiting for: " + timeDelta);
                try {
                    Thread.sleep(timeDelta);
                } catch (InterruptedException e) {
                    mMockerService.signalDeathAndMaybeBroadcastTermination();
                    e.printStackTrace();
                }
            }
        }
        mMockerService.signalDeathAndMaybeBroadcastTermination();
    }

}
