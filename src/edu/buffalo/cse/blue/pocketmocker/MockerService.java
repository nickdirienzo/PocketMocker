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
			Message testMsg = Message.obtain();
			Bundle data = new Bundle();
			data.putString("String", "Hey there!");
			testMsg.setData(data);
			try {
				msg.replyTo.send(testMsg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (app.isReplaying()) {
				MockLocation mockLoc = mockLocationManager.getNext();
				if (mockLoc == null) {
					app.setIsReplaying(PocketMockerApplication.DO_NOT_REPLAY);
				} else {
					Log.v(TAG,
							"Location to return: "
									+ app.buildLocationDisplayString(mockLoc.getRealLocation()));
				}
			}
			super.handleMessage(msg);
		}
	}

}
