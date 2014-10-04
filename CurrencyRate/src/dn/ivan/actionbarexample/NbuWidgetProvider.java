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
import dn.ivan.actionbarexample.logic.NbuRates;
import dn.ivan.actionbarexample.service.BackgroundService;

public class NbuWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.NBU_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		
		context.startService(intent);
		
		// //////////////////////////////////////////////
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
            int appWidgetId = appWidgetIds[i];

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra(MainActivity.SOURCE, MainActivity.NBU_SOURCE);
            intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 301, intent2, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nbu_widget);
            views.setOnClickPendingIntent(R.id.remote_view_currencys_nbu, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }		
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDeleted(Context context, int[] appWidgetIds) {}
	public void onEnabled(Context context) {}
	public void onDisabled(Context context) {}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void updateWidget(ArrayList<Object> rates, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nbu_widget);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			NbuRates ratesItem = (NbuRates) rates.get(i);
			
			if ("USD".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.usd_lbl_nbu, "USD");
				views.setTextViewText(R.id.usd_txt_nbu, new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.usd_direction_nbu, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.usd_direction_nbu, R.drawable.down);
				}
			}			
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			NbuRates ratesItem = (NbuRates) rates.get(i);
			
			if ("EUR".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.eur_lbl_nbu, "EUR");
				views.setTextViewText(R.id.eur_txt_nbu, new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.eur_direction_nbu, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.eur_direction_nbu, R.drawable.down);
				}
			}			
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			NbuRates ratesItem = (NbuRates) rates.get(i);
			
			if ("RUB".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.rub_lbl_nbu, "RUB");
				views.setTextViewText(R.id.rub_txt_nbu, "  " + new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString());
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.rub_direction_nbu, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.rub_direction_nbu, R.drawable.down);
				}
			}
		}		
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		views.setTextViewText(R.id.date_lbl_nbu, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
		//views.setTextViewText(R.id.date_lbl_nbu, context.getResources().getString(R.string.title_section1));
		
		views.setViewVisibility(R.id.progressBar_widget_nbu, ProgressBar.INVISIBLE);
		views.setViewVisibility(R.id.refresh_widget_nbu, ProgressBar.VISIBLE);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.NBU_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		views.setOnClickPendingIntent(R.id.refresh_widget_nbu, PendingIntent.getService(context, 301, intent, 0));
		
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
		ComponentName thisWidget = new ComponentName(context.getApplicationContext(), NbuWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        if (MainActivity.START_LOAD.equalsIgnoreCase(intent.getAction())
        		&& MainActivity.NBU_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
        		&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
	        	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nbu_widget);
				views.setViewVisibility(R.id.progressBar_widget_nbu, ProgressBar.VISIBLE);
				views.setViewVisibility(R.id.refresh_widget_nbu, ProgressBar.INVISIBLE);
				
				appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
		}
		
		if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") == null
				&& MainActivity.NBU_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(MainActivity.RATES);
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	updateWidget(rates, context, appWidgetManager, appWidgetIds[i]);
			}
			
			stopService(context);
		}
		else if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") != null
				&& MainActivity.NBU_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nbu_widget);
				
				//views.setTextViewText(R.id.date_lbl_nbu, "");
				//views.setTextViewText(R.id.date_lbl_nbu, context.getResources().getString(R.string.title_section1));
				
				views.setViewVisibility(R.id.progressBar_widget_nbu, ProgressBar.INVISIBLE);
				views.setViewVisibility(R.id.refresh_widget_nbu, ProgressBar.VISIBLE);
				
				/*views.setTextViewText(R.id.usd_lbl_nbu, "Error");
				views.setTextViewText(R.id.usd_txt_nbu, "");
				views.setTextViewText(R.id.eur_lbl_nbu, "");
				views.setTextViewText(R.id.eur_txt_nbu, "");
				views.setTextViewText(R.id.rub_lbl_nbu, "");
				views.setTextViewText(R.id.rub_txt_nbu, "");*/
				
				// /////////////////////////////////////////////////////////////////////////////////////////
				
				Intent intent2 = new Intent(context, BackgroundService.class);
				intent2.putExtra(MainActivity.SOURCE, MainActivity.NBU_SOURCE);
				intent2.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
				views.setOnClickPendingIntent(R.id.refresh_widget_nbu, PendingIntent.getService(context, 301, intent2, 0));
				
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