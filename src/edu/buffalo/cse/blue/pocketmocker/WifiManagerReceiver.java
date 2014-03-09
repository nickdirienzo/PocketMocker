
package edu.buffalo.cse.blue.pocketmocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.models.MockWifiManager;

public class WifiManagerReceiver extends BroadcastReceiver {

    private MockWifiManager mMockWifiManager;

    /**
     * Must be started programmatically in PocketMocker.
     * 
     * @param context PocketMocker context
     */
    public WifiManagerReceiver(Context context) {
        mMockWifiManager = MockWifiManager.getInstance(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
        } else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
        } else if(intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
        } else if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
        }
    }

}
