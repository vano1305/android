package dn.ivan.actionbarexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dn.ivan.actionbarexample.MainActivity;

public class BackgroundService extends Service {
	
	private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
	
	@Override
	public void onCreate() {
		super.onCreate();		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
		
		if (intent.getExtras() != null && intent.getExtras().getString(MainActivity.SOURCE) != null && intent.getExtras().getString(MainActivity.FROM) != null) {
			
			String source = intent.getExtras().getString(MainActivity.SOURCE);
			String from = intent.getExtras().getString(MainActivity.FROM);
						
			new Thread(myThreads, new ServiceWorker(BackgroundService.this, source, from), "BackgroundService").start();
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