package dn.ivan.actionbarexample;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import android.content.SharedPreferences;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.MetalsRates;
import dn.ivan.actionbarexample.services.BackgroundService;

public class MetalsWidgetProvider extends AppWidgetProvider {
	
	static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());		
	static {
		
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(' ');
	}
	
	static DecimalFormat df = new DecimalFormat("###,###,##0.00", dfs);
	static {
		df.setGroupingSize(3);
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.METALS_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		
		context.startService(intent);
		
		// //////////////////////////////////////////////
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
            int appWidgetId = appWidgetIds[i];

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra(MainActivity.SOURCE, MainActivity.METALS_SOURCE);
            //intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 401, intent2, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metals_widget);
            views.setOnClickPendingIntent(R.id.remote_view_currencys_metals, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }		
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDeleted(Context context, int[] appWidgetIds) {}
	public void onEnabled(Context context) {}
	public void onDisabled(Context context) {}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void updateWidget(ArrayList<Object> rates, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metals_widget);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		int scale = Integer.valueOf(getValueFromPref(context, "selected_metals_scale", "0"));
		
		for (int i = 0; i < rates.size(); i++) {
			
			MetalsRates ratesItem = (MetalsRates) rates.get(i);
			
			if ("XAU".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.xau_lbl_metals, "XAU");
				if (scale == 0) {
					views.setTextViewText(R.id.xau_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				else {
					views.setTextViewText(R.id.xau_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal("31.1034768"), 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.xau_direction_metals, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.xau_direction_metals, R.drawable.down);
				}
			}			
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			MetalsRates ratesItem = (MetalsRates) rates.get(i);
			
			if ("XAG".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.xag_lbl_metals, "XAG");
				if (scale == 0) {
					views.setTextViewText(R.id.xag_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				else {
					views.setTextViewText(R.id.xag_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal("31.1034768"), 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.xag_direction_metals, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.xag_direction_metals, R.drawable.down);
				}
			}			
		}
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			MetalsRates ratesItem = (MetalsRates) rates.get(i);
			
			if ("XPT".equalsIgnoreCase(ratesItem.char3)) {
				
				views.setTextViewText(R.id.xpt_lbl_metals, "XPT");
				if (scale == 0) {
					views.setTextViewText(R.id.xpt_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				else {
					views.setTextViewText(R.id.xpt_txt_metals, df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal(ratesItem.size)).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal("31.1034768"), 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
				}
				
				if (Double.valueOf(ratesItem.change) > 0) {					
					views.setImageViewResource(R.id.xpt_direction_metals, R.drawable.up);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {
					views.setImageViewResource(R.id.xpt_direction_metals, R.drawable.down);
				}
			}
		}		
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		views.setTextViewText(R.id.date_lbl_metals, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
		//views.setTextViewText(R.id.date_lbl_metals, context.getResources().getString(R.string.title_section3));
		
		views.setViewVisibility(R.id.progressBar_widget_metals, ProgressBar.INVISIBLE);
		views.setViewVisibility(R.id.refresh_widget_metals, ProgressBar.VISIBLE);
		
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		Intent intent = new Intent(context, BackgroundService.class);
		intent.putExtra(MainActivity.SOURCE, MainActivity.METALS_SOURCE);
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
		views.setOnClickPendingIntent(R.id.refresh_widget_metals, PendingIntent.getService(context, 401, intent, 0));
		
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
		ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MetalsWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        
        if (MainActivity.START_LOAD.equalsIgnoreCase(intent.getAction())
        		&& MainActivity.METALS_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
        		&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
	        	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metals_widget);
				views.setViewVisibility(R.id.progressBar_widget_metals, ProgressBar.VISIBLE);
				views.setViewVisibility(R.id.refresh_widget_metals, ProgressBar.INVISIBLE);
				
				appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
		}
		
		if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") == null
				&& MainActivity.METALS_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(MainActivity.RATES);
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	updateWidget(rates, context, appWidgetManager, appWidgetIds[i]);
			}
			
			stopService(context);
		}
		else if (MainActivity.FINISH_LOAD.equalsIgnoreCase(intent.getAction())
				&& intent.getExtras().getString("error") != null
				&& MainActivity.METALS_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))
				&& MainActivity.FROM_WIDGET.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))) {
			
			for (int i=0; i < appWidgetIds.length; i++) {
	        	
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metals_widget);
				
				//views.setTextViewText(R.id.date_lbl_nbu, "");
				//views.setTextViewText(R.id.date_lbl_metals, context.getResources().getString(R.string.title_section3));
				
				views.setViewVisibility(R.id.progressBar_widget_metals, ProgressBar.INVISIBLE);
				views.setViewVisibility(R.id.refresh_widget_metals, ProgressBar.VISIBLE);
				
				/*views.setTextViewText(R.id.xau_lbl_metals, "Error");
				views.setTextViewText(R.id.xau_txt_metals, "");
				views.setTextViewText(R.id.xag_lbl_metals, "");
				views.setTextViewText(R.id.xag_txt_metals, "");
				views.setTextViewText(R.id.xpt_lbl_metals, "");
				views.setTextViewText(R.id.xpt_txt_metals, "");*/
				
				// /////////////////////////////////////////////////////////////////////////////////////////
				
				Intent intent2 = new Intent(context, BackgroundService.class);
				intent2.putExtra(MainActivity.SOURCE, MainActivity.METALS_SOURCE);
				intent2.putExtra(MainActivity.FROM, MainActivity.FROM_WIDGET);
				views.setOnClickPendingIntent(R.id.refresh_widget_metals, PendingIntent.getService(context, 401, intent2, 0));
				
				// /////////////////////////////////////////////////////////////////////////////////////////
			    
			    appWidgetManager.updateAppWidget(appWidgetIds[i], views);
			}
			
			stopService(context);
		}
    }
	
	public void stopService(Context context) {
		context.stopService(new Intent(context, BackgroundService.class));
	}
	
	protected String getValueFromPref(Context context, String prefName, String defValue) {
		
		SharedPreferences shared = context.getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		String value = shared.getString(prefName, defValue);
		
		return value;
	}
}