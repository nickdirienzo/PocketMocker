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
			Messenger replyTo = msg.replyTo;
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
				sendMockLocation(oldLoc, replyTo);
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
			sendMockLocation(nextLoc, replyTo);
			Log.v(TAG, "No more mocked locations! Old loc: " + oldLoc.getId() + " Next loc: "
					+ nextLoc.getId());
			super.handleMessage(msg);
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
