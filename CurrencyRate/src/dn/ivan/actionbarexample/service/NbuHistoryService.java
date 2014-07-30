package dn.ivan.actionbarexample.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import dn.ivan.actionbarexample.receivers.UpdateNbuHistoryReceiver;

public class NbuHistoryService extends Service {

	private static final int INTERVAL = 10000;
	private static final int FIRST_RUN = 5000;
	private int REQUEST_CODE = 11223344;

	AlarmManager alarmManager;

	@Override
	public void onCreate() {
		
		super.onCreate();

		Log.v(this.getClass().getName(), "onCreate()");
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		Log.v(this.getClass().getName(), "onBind()");
		return null;
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		if (alarmManager != null) {
			Intent intent = new Intent(this, UpdateNbuHistoryReceiver.class);
			alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
		}
		
		Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
		
		startService();		
		
		return START_STICKY;
	}

	private void startService() {

		Intent intent = new Intent(this, UpdateNbuHistoryReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FIRST_RUN, INTERVAL, pendingIntent);

		Log.v(this.getClass().getName(), "Service started at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
	}
}