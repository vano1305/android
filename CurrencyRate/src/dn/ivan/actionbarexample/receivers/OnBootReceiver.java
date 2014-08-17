package dn.ivan.actionbarexample.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dn.ivan.actionbarexample.service.HistoryService;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			
			Intent serviceLauncher = new Intent(context, HistoryService.class);
			context.startService(serviceLauncher);
		}
	}
}