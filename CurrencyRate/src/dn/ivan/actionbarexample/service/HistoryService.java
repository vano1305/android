package dn.ivan.actionbarexample.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import dn.ivan.actionbarexample.receivers.LoadHistoryReceiver;

public class HistoryService extends Service {

	private static final int INTERVAL = 14400000;
	private static final int FIRST_RUN = 5000;
	private int REQUEST_CODE = 11223344;

	AlarmManager alarmManager;

	@Override
	public void onCreate() {		
		super.onCreate();
		
		startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		if (alarmManager != null) {
			
			Intent intent = new Intent(this, LoadHistoryReceiver.class);
			alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
		}
	}
	
	/*@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
		
		startService();		
		
		return START_STICKY;
	}*/

	private void startService() {

		Intent intent = new Intent(this, LoadHistoryReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FIRST_RUN, INTERVAL, pendingIntent);
	}
}