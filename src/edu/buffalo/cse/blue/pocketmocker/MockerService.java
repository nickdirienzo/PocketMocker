package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocation;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;

public class MockerService extends Service {

	public static final String TAG = "MOCK_SRV";

	private final Messenger messenger = new Messenger(new IncomingHandler());
	private PocketMockerApplication app;
	private MockLocationManager mockLocationManager;

	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Mocker service started.", Toast.LENGTH_LONG)
				.show();
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
			if (app.isReplaying()) {
				MockLocation mockLoc = mockLocationManager.getNext();
				if (mockLoc == null) {
					app.setIsReplaying(app.DO_NOT_REPLAY);
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
