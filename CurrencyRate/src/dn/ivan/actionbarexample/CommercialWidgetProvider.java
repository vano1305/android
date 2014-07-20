package dn.ivan.actionbarexample;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import dn.ivan.actionbarexample.fragments.logic.CommercialRates;
import dn.ivan.actionbarexample.fragments.logic.NotifyManager;
import dn.ivan.actionbarexample.service.BackgroundService;

public class CommercialWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
		context.startService(intent);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDeleted(Context context, int[] appWidgetIds) {		
		stopService(context);
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onEnabled(Context context) {
		
		
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDisabled(Context context) {
		
		
	}
	
	public void updateWidget(ArrayList<Object> rates, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_widget);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		double totalBuy = 0.0;
		double totalSell = 0.0;
		
		int count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"USD".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();		    
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();		    
		}
		views.setTextViewText(R.id.usd_lbl, "USD");
		views.setTextViewText(R.id.usd_txt, new BigDecimal(totalBuy/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (!"".equalsIgnoreCase(loadCurrencyRateFromPref(context, "USD"))) {
    		
    		double oldSellRate = Double.valueOf(loadCurrencyRateFromPref(context, "USD"));
    		if (oldSellRate != new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()) {
    			sendNotification(
    					context,
    					"Продажа USD: " + ((new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() - oldSellRate) > 0? ("+" + new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()): new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()),
    					"USD");
    		}
    	}		
		saveCurrencyRate2Pref(context, "USD", new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		totalBuy = 0.0;
		totalSell = 0.0;
		
		count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"EUR".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();		    
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		views.setTextViewText(R.id.eur_lbl, "EUR");
		views.setTextViewText(R.id.eur_txt, new BigDecimal(totalBuy/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (!"".equalsIgnoreCase(loadCurrencyRateFromPref(context, "EUR"))) {
    		
    		double oldSellRate = Double.valueOf(loadCurrencyRateFromPref(context, "EUR"));
    		if (oldSellRate != new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()) {
    			sendNotification(
    					context,
    					"Продажа EUR: " + ((new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() - oldSellRate) > 0? ("+" + new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()): new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()),
    					"EUR");
    		}
    	}		
		saveCurrencyRate2Pref(context, "EUR", new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		totalBuy = 0.0;
		totalSell = 0.0;
		
		count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"RUB".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}	
		views.setTextViewText(R.id.rub_lbl, "RUB");
		views.setTextViewText(R.id.rub_txt, new BigDecimal(totalBuy/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (!"".equalsIgnoreCase(loadCurrencyRateFromPref(context, "RUB"))) {
    		
    		double oldSellRate = Double.valueOf(loadCurrencyRateFromPref(context, "RUB"));
    		if (oldSellRate != new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()) {
    			sendNotification(
    					context,
    					"Продажа RUB: " + ((new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() - oldSellRate) > 0? ("+" + new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()): new BigDecimal(totalSell/count).subtract(new BigDecimal(oldSellRate)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()),
    					"RUB");
    		}
    	}		
		saveCurrencyRate2Pref(context, "RUB", new BigDecimal(totalSell/count).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		views.setTextViewText(R.id.date_lbl, ((CommercialRates) rates.get(0)).date);
		
		views.setViewVisibility(R.id.progressBar_widget, ProgressBar.INVISIBLE);
		views.setViewVisibility(R.id.refresh_widget, ProgressBar.VISIBLE);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
		views.setOnClickPendingIntent(R.id.refresh_widget, PendingIntent.getService(context, 0, intent, 0));
		
		// /////////////////////////////////////////////////////////////////////////////////////////
	    
	    appWidgetManager.updateAppWidget(appWidgetId, views);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	@Override
    public void onReceive(Context context, Intent intent) {
        
		super.onReceive(context, intent);
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(context.getApplicationContext(), CommercialWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        if (MainActivity.START_LOAD.equalsIgnoreCase(intent.getAction()) && MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
	        	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_widget);
				views.setViewVisibility(R.id.progressBar_widget, ProgressBar.VISIBLE);
				views.setViewVisibility(R.id.refresh_widget, ProgressBar.INVISIBLE);
				
				appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
		}
		
		if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction()) && intent.getExtras().getString("error") == null && MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
			
			ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(MainActivity.RATES);
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	updateWidget(rates, context, appWidgetManager, appWidgetIds[i]);
			}
		}
		else if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction()) && intent.getExtras().getString("error") != null && MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.currency_widget);
				
				views.setTextViewText(R.id.date_lbl, "");
				
				views.setViewVisibility(R.id.progressBar_widget, ProgressBar.INVISIBLE);
				views.setViewVisibility(R.id.refresh_widget, ProgressBar.VISIBLE);
				
				views.setTextViewText(R.id.usd_lbl, "Нет соединения");
				views.setTextViewText(R.id.usd_txt, "");
				views.setTextViewText(R.id.eur_lbl, "");
				views.setTextViewText(R.id.eur_txt, "");
				views.setTextViewText(R.id.rub_lbl, "");
				views.setTextViewText(R.id.rub_txt, "");
				
				// /////////////////////////////////////////////////////////////////////////////////////////
				
				Intent intent2 = new Intent(context, BackgroundService.class);
				intent2.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
				views.setOnClickPendingIntent(R.id.refresh_widget, PendingIntent.getService(context, 0, intent2, 0));
				
				// /////////////////////////////////////////////////////////////////////////////////////////
			    
			    appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}			
		}
    }
	
	public void stopService(Context context) {
		context.stopService(new Intent(context, BackgroundService.class));
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("deprecation")
	protected void sendNotification(Context context, String message, String currencyCode) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (sp.getBoolean("notif", false)) {
			
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Intent notificationIntent = new Intent(context, MainActivity.class);
			
			NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
			
			nb.setSmallIcon(R.drawable.commercial_small);
			nb.setAutoCancel(true);
			nb.setTicker("Изменился курс " + currencyCode);
			nb.setContentText(message);
			nb.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));
			nb.setWhen(System.currentTimeMillis());
			nb.setContentTitle("Курсы валют");
			nb.setDefaults(Notification.DEFAULT_LIGHTS);
			
			Notification notification = nb.getNotification();
			notificationManager.notify(NotifyManager.i, notification);
			
			NotifyManager.i++;			
		}
	}
	
	protected void saveCurrencyRate2Pref(Context context, String currencyCode, String rate) {
		
		SharedPreferences shared = context.getSharedPreferences(currencyCode, MainActivity.MODE_PRIVATE);
		Editor ed = shared.edit();
		ed.remove(currencyCode);
		ed.putString(currencyCode, rate);
		ed.commit();
	}

	protected String loadCurrencyRateFromPref(Context context, String currencyCode) {
		
		SharedPreferences shared = context.getSharedPreferences(currencyCode, MainActivity.MODE_PRIVATE);
		String currencyRate = shared.getString(currencyCode, "");
		
		return currencyRate;
	}
}