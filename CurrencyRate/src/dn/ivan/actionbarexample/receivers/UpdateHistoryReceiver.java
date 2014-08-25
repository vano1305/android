package dn.ivan.actionbarexample.receivers;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.logic.DataManager;

public class UpdateHistoryReceiver extends BroadcastReceiver {

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (MainActivity.UPDATE_HISTORY.equalsIgnoreCase(intent.getAction()) && intent.getExtras().getString("error") == null && MainActivity.NBU_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
			
			ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(MainActivity.RATES);
			new DataManager().saveNBURates2DB(context, rates);			
		}
	}
}