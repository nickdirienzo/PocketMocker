package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class TestMockerServiceActivity extends Activity {

	private final String TAG = MockerService.TAG + "_ACT";
	private Messenger messengerService = null;
	private boolean isServiceBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_mocker_service_activity);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.doUnbindService();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.doBindService();
	}

	public void sendMessage(View view) {
		Log.v(TAG, "Sending message");
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString("MSG", "YOSUPDUDE");
		msg.setData(data);
		msg.replyTo = messenger;
		try {
			messengerService.send(msg);
			Log.v(TAG, "Message sent.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "Message received: " + msg.toString());
		}
	}

	private final Messenger messenger = new Messenger(new IncomingHandler());

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			messengerService = new Messenger(service);
			Log.v(TAG, "Connected to mocker service.");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			messengerService = null;
			Log.v(TAG, "Mocker service crashed. Disconnecting.");
		}

	};

	private void doBindService() {
		this.bindService(new Intent(TestMockerServiceActivity.this, MockerService.class),
				connection, Context.BIND_AUTO_CREATE);
		isServiceBound = true;
		Log.v(TAG, "Binding to mocker service");
	}
	
	private void doUnbindService() {
		this.unbindService(connection);
		isServiceBound = false;
		Log.v(TAG, "Unbound from mocker service.");
	}
}
