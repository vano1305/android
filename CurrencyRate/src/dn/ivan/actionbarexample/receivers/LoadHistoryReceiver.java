package dn.ivan.actionbarexample.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.logic.ServiceWorker;

public class LoadHistoryReceiver extends BroadcastReceiver {
	
	private ThreadGroup myThreads = new ThreadGroup("LoadHistory");

	@Override
	public void onReceive(Context context, Intent intent) {		
		new Thread(myThreads, new ServiceWorker(context, MainActivity.NBU_SOURCE, MainActivity.FROM_SERVICE_HISTORY, "", "", "", "", ""), "LoadHistory").start();		
	}
}