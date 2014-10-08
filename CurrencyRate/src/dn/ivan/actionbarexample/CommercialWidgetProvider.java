package dn.ivan.actionbarexample;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.CommercialRates;
import dn.ivan.actionbarexample.services.BackgroundService;

public class CommercialWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		
		context.startService(intent);
		
		// //////////////////////////////////////////////
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
            int appWidgetId = appWidgetIds[i];

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
            //intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 501, intent2, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.commercial_widget);
            views.setOnClickPendingIntent(R.id.remote_view_currencys_commercial, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }		
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDeleted(Context context, int[] appWidgetIds) {
		
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onEnabled(Context context) {	    
	    		
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDisabled(Context context) {		
		
	}
	
	public void updateWidget(ArrayList<Object> rates, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.commercial_widget);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		double totalBuy = 0.0;
		double totalSell = 0.0;
		double totalBuyChange = 0.0;
		double totalSellChange = 0.0;
		
		int count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"USD".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalBuyChange = new BigDecimal(totalBuyChange).add(new BigDecimal(ratesItem.rateBuyDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSellChange = new BigDecimal(totalSellChange).add(new BigDecimal(ratesItem.rateSaleDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		views.setTextViewText(R.id.usd_lbl_commercial, "USD");
		views.setTextViewText(R.id.usd_txt_commercial, new BigDecimal(totalBuy/count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (Double.valueOf(totalBuyChange / count) > 0) {					
			views.setImageViewResource(R.id.usd_buy_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalBuyChange / count) < 0) {
			views.setImageViewResource(R.id.usd_buy_direction_commercial, R.drawable.down);
		}
		
		if (Double.valueOf(totalSellChange / count) > 0) {					
			views.setImageViewResource(R.id.usd_sell_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalSellChange / count) < 0) {
			views.setImageViewResource(R.id.usd_sell_direction_commercial, R.drawable.down);
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		totalBuy = 0.0;
		totalSell = 0.0;
		totalBuyChange = 0.0;
		totalSellChange = 0.0;
		
		count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"EUR".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();		    
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalBuyChange = new BigDecimal(totalBuyChange).add(new BigDecimal(ratesItem.rateBuyDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSellChange = new BigDecimal(totalSellChange).add(new BigDecimal(ratesItem.rateSaleDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		views.setTextViewText(R.id.eur_lbl_commercial, "EUR");
		views.setTextViewText(R.id.eur_txt_commercial, new BigDecimal(totalBuy/count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (Double.valueOf(totalBuyChange / count) > 0) {					
			views.setImageViewResource(R.id.eur_buy_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalBuyChange / count) < 0) {
			views.setImageViewResource(R.id.eur_buy_direction_commercial, R.drawable.down);
		}
		
		if (Double.valueOf(totalSellChange / count) > 0) {					
			views.setImageViewResource(R.id.eur_sell_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalSellChange / count) < 0) {
			views.setImageViewResource(R.id.eur_sell_direction_commercial, R.drawable.down);
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		totalBuy = 0.0;
		totalSell = 0.0;
		totalBuyChange = 0.0;
		totalSellChange = 0.0;
		
		count = 0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			if (!"RUB".equalsIgnoreCase(ratesItem.codeAlpha)) {
				continue;
			}
			
			count ++;
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalBuyChange = new BigDecimal(totalBuyChange).add(new BigDecimal(ratesItem.rateBuyDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSellChange = new BigDecimal(totalSellChange).add(new BigDecimal(ratesItem.rateSaleDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}	
		views.setTextViewText(R.id.rub_lbl_commercial, "RUB");
		views.setTextViewText(R.id.rub_txt_commercial, new BigDecimal(totalBuy/count).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "/" + new BigDecimal(totalSell/count).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString());
		
		if (Double.valueOf(totalBuyChange / count) > 0) {					
			views.setImageViewResource(R.id.rub_buy_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalBuyChange / count) < 0) {
			views.setImageViewResource(R.id.rub_buy_direction_commercial, R.drawable.down);
		}
		
		if (Double.valueOf(totalSellChange / count) > 0) {					
			views.setImageViewResource(R.id.rub_sell_direction_commercial, R.drawable.up);
		}
		else if (Double.valueOf(totalSellChange / count) < 0) {
			views.setImageViewResource(R.id.rub_sell_direction_commercial, R.drawable.down);
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		views.setTextViewText(R.id.date_lbl_commercial, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
		//views.setTextViewText(R.id.date_lbl_commercial, context.getResources().getString(R.string.title_section2));
		
		views.setViewVisibility(R.id.progressBar_widget_commercial, ProgressBar.INVISIBLE);
		views.setViewVisibility(R.id.refresh_widget_commercial, ProgressBar.VISIBLE);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		views.setOnClickPendingIntent(R.id.refresh_widget_commercial, PendingIntent.getService(context, 501, intent, 0));
		
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
        
        if (MainActivity.START_LOAD.equalsIgnoreCase(intent.getAction())
        		&& MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
        		&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
	        	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.commercial_widget);
				views.setViewVisibility(R.id.progressBar_widget_commercial, ProgressBar.VISIBLE);
				views.setViewVisibility(R.id.refresh_widget_commercial, ProgressBar.INVISIBLE);
				
				appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
		}
		
		if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") == null
				&& MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(MainActivity.RATES);
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	updateWidget(rates, context, appWidgetManager, appWidgetIds[i]);
			}
			
			stopService(context);
		}
		else if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") != null
				&& MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.commercial_widget);
				
				//views.setTextViewText(R.id.date_lbl_commercial, "");
				//views.setTextViewText(R.id.date_lbl_commercial, context.getResources().getString(R.string.title_section2));
				
				views.setViewVisibility(R.id.progressBar_widget_commercial, ProgressBar.INVISIBLE);
				views.setViewVisibility(R.id.refresh_widget_commercial, ProgressBar.VISIBLE);
				
				/*views.setTextViewText(R.id.usd_lbl_commercial, "Error");
				views.setTextViewText(R.id.usd_txt_commercial, "");
				views.setTextViewText(R.id.eur_lbl_commercial, "");
				views.setTextViewText(R.id.eur_txt_commercial, "");
				views.setTextViewText(R.id.rub_lbl_commercial, "");
				views.setTextViewText(R.id.rub_txt_commercial, "");*/
				
				// /////////////////////////////////////////////////////////////////////////////////////////
				
				Intent intent2 = new Intent(context, BackgroundService.class);
				intent2.putExtra(MainActivity.SOURCE, MainActivity.COMMERCIAL_SOURCE);
				intent2.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
				views.setOnClickPendingIntent(R.id.refresh_widget_commercial, PendingIntent.getService(context, 501, intent2, 0));
				
				// /////////////////////////////////////////////////////////////////////////////////////////
			    
			    appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
			
			stopService(context);
		}
    }
	
	public void stopService(Context context) {
		context.stopService(new Intent(context, BackgroundService.class));
	}
}