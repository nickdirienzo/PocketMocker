package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MockerService extends Service {
	
	private final Messenger messenger = new Messenger(new IncomingHandler());
	
	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Mocker service started.", Toast.LENGTH_LONG).show();	
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return messenger.getBinder();
	}
	
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getApplicationContext(), "MSG RECEIVED", Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	}

}
