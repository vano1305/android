package dn.ivan.actionbarexample.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateNbuHistoryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.v(this.getClass().getName(), "Timed alarm onRecieve() started");
	}
}