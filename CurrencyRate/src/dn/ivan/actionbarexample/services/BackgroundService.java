package dn.ivan.actionbarexample.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.logic.ServiceWorker;

public class BackgroundService extends Service {
	
	private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
	
	@Override
	public void onCreate() {
		super.onCreate();		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
		
		if (intent != null && intent.getExtras() != null && intent.getExtras().getString(MainActivity.SOURCE) != null && intent.getExtras().getString(MainActivity.FROM) != null) {
			
			String source = intent.getExtras().getString(MainActivity.SOURCE);
			String from = intent.getExtras().getString(MainActivity.FROM);
			
			String regionCode = "";
			if (intent.getExtras().getString(MainActivity.REGION) != null) {
				regionCode = intent.getExtras().getString(MainActivity.REGION);
			}
			
			String cityCode = "";
			if (intent.getExtras().getString(MainActivity.CITY) != null) {
				cityCode = intent.getExtras().getString(MainActivity.CITY);
			}
			
			
			String currencyCode = "";
			if (intent.getExtras().getString(MainActivity.CURRENCY) != null) {
				currencyCode = intent.getExtras().getString(MainActivity.CURRENCY);
			}
			String date1 = "";
			if (intent.getExtras().getString(MainActivity.DATE1) != null) {
				date1 = intent.getExtras().getString(MainActivity.DATE1);
			}
			String date2 = "";
			if (intent.getExtras().getString(MainActivity.DATE2) != null) {
				date2 = intent.getExtras().getString(MainActivity.DATE2);
			}
						
			new Thread(myThreads, new ServiceWorker(BackgroundService.this, source, from, regionCode, cityCode, currencyCode, date1, date2), "BackgroundService").start();
		}		
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		
		myThreads.interrupt();
				
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}