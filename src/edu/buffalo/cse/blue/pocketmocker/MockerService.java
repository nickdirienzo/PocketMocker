package edu.buffalo.cse.blue.pocketmocker;

import java.util.HashMap;

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

public class MockerService extends Service {

	public static final String TAG = "MOCK_SRV";

	private final Messenger messenger = new Messenger(new IncomingHandler());
	private PocketMockerApplication app;
	private MockLocationManager mockLocationManager;
	private MockLocation oldLoc;
	private MockLocation nextLoc;

	private final HashMap<String, Messenger> clients = new HashMap<String, Messenger>();

	@Override
	public void onCreate() {
		Log.v(TAG, "Mocker service started.");
		mockLocationManager = MockLocationManager.getInstance(getApplicationContext());
		app = (PocketMockerApplication) getApplicationContext();
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
			long timeToWait = 0;
			Bundle data = msg.getData();
			// This should be fine because even if one APK requests multiple
			// LocationListeners to the LocationManager, we don't care, we just
			// need to send their Context data.
			String clientPackage = data.getString("package");
			Messenger replyTo;
			if (clients.containsKey(clientPackage)) {
				replyTo = clients.get(clientPackage);
			} else {
				replyTo = msg.replyTo;
				clients.put(clientPackage, replyTo);
			}
			// Stupid hack for now
			if (!mockLocationManager.isReady()) {
				mockLocationManager.init();
			}
			while (mockLocationManager.hasNext()) {
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
					e.printStackTrace();
				}
			}
			// By this point oldLoc has been sent, but nextLoc is our last
			// location to send.
			broadcastMockLocation(nextLoc);
			Log.v(TAG, "No more mocked locations! Old loc: " + oldLoc.getId() + " Next loc: "
					+ nextLoc.getId());
		}
	}

	private void broadcastMockLocation(MockLocation m) {
		for (Messenger client : clients.values()) {
			sendMockLocation(m, client);
		}
	}

	private void sendMockLocation(MockLocation mLoc, Messenger m) {
		Bundle locToSend = mLoc.toBundle(System.currentTimeMillis());
		Message reply = Message.obtain();
		reply.setData(locToSend);
		try {
			Log.v(TAG, "Sending location for mock: " + mLoc.getId());
			m.send(reply);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
