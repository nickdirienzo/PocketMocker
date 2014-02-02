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

public class MockerService extends Service {

	public static final String TAG = "MOCK_SRV";

	private final Messenger messenger = new Messenger(new IncomingHandler());
	private PocketMockerApplication app;
	private MockLocationManager mockLocationManager;
	private MockLocation oldLoc;
	private MockLocation nextLoc;

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
			// Stupid hack for now
			mockLocationManager.init();
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
				Bundle locToSend = oldLoc.toBundle(System.currentTimeMillis());
				Message m = Message.obtain();
				m.setData(locToSend);
				try {
					Log.v(TAG, "Sending: " + locToSend);
					msg.replyTo.send(m);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				try {
					Log.v(TAG, "Sleeping for " + timeToWait);
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.v(TAG, "No more mocked locations! Old loc: " + oldLoc.getId() + " Next loc: "
					+ nextLoc.getId());
			super.handleMessage(msg);
		}
	}

}
