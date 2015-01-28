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
						
			new Thread(myThreads, new ServiceWorker(BackgroundService.this, source, from, regionCode, cityCode), "BackgroundService").start();
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