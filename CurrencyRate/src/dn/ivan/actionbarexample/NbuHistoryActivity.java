package dn.ivan.actionbarexample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import dn.ivan.actionbarexample.fragments.HistoryFragment;
import dn.ivan.actionbarexample.logic.NbuHistoryItem;
import dn.ivan.actionbarexample.logic.NbuRatesHolderForChart;
import dn.ivan.actionbarexample.logic.NetworkManager;
import dn.ivan.actionbarexample.services.BackgroundService;

public class NbuHistoryActivity extends SherlockFragmentActivity {
	
	private ProgressDialog pd;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	private BroadcastReceiver br1;
	private BroadcastReceiver br2;
	
	Toast connectionToast = null;
	
	private static final int CONTENT_VIEW_ID = 10101010;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
        	
            HistoryFragment historyFragment = (HistoryFragment) Fragment.instantiate(this, HistoryFragment.class.getName());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(CONTENT_VIEW_ID, historyFragment, MainActivity.HISTORY_FRAGMENT).commit();
        }
				
		ActionBar actionBar = getSupportActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.title_section6);
		actionBar.setDisplayUseLogoEnabled(false);
		
		registerReceiver();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
	    		
	    	default:
	    		break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void loadNbuHistory() {
		
		if (!NetworkManager.checkInternetConnection(this)) {
			
			connectionToast = Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG);
			connectionToast.show();
			return;
		}

		Intent intent = new Intent(NbuHistoryActivity.this, BackgroundService.class);
		
		intent.putExtra(MainActivity.CURRENCY, ((HistoryFragment)getSupportFragmentManager().findFragmentByTag(MainActivity.HISTORY_FRAGMENT)).getCurrencyCode());
		intent.putExtra(MainActivity.DATE1, ((HistoryFragment)getSupportFragmentManager().findFragmentByTag(MainActivity.HISTORY_FRAGMENT)).getDate1());
		intent.putExtra(MainActivity.DATE2, ((HistoryFragment)getSupportFragmentManager().findFragmentByTag(MainActivity.HISTORY_FRAGMENT)).getDate2());
		intent.putExtra(MainActivity.SOURCE, MainActivity.NBU_HISTORY_SOURCE);		
		intent.putExtra(MainActivity.FROM, MainActivity.FROM_APPLICATION);
		
		startService(intent);
	}
	
	public void registerReceiver() {
		
		br1 = new BroadcastReceiver() {

			@SuppressWarnings("unchecked")
			public void onReceive(Context context, Intent intent) {
				
				if (intent.getExtras().getString(MainActivity.FROM) != null
						&& MainActivity.FROM_APPLICATION.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))
						&& intent.getExtras().getString(MainActivity.SOURCE) != null
						&& MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
					
					if (intent.getExtras().getString("error") == null) {
						
						ArrayList<NbuHistoryItem> rates = (ArrayList<NbuHistoryItem>)intent.getExtras().getSerializable(MainActivity.RATES);
						if (rates != null && rates.size() == 1 && !"".equalsIgnoreCase(rates.get(0).history)) {
							
							ArrayList<NbuRatesHolderForChart> ratesHolder = new ArrayList<NbuRatesHolderForChart>();
							
							String[] items1 = rates.get(0).history.split(";");
							for (int i = 0; i < items1.length; i++) {
								
								String[] items2 = items1[i].split(":");
								
								NbuRatesHolderForChart nbuHistoryItem = new NbuRatesHolderForChart();
								try {
									nbuHistoryItem.date = sdf.parse(items2[0]);
									nbuHistoryItem.rate = Double.valueOf(items2[1]);
								}
								catch (ParseException e) {
									e.printStackTrace();
								}
								ratesHolder.add(nbuHistoryItem);
							}
							
							((HistoryFragment)getSupportFragmentManager().findFragmentByTag(MainActivity.HISTORY_FRAGMENT)).createChart(ratesHolder);
						}
					}
					
					hideProgress();
				}
			}
		};
		registerReceiver(br1, new IntentFilter(MainActivity.FINISH_LOAD));

		// ////////////////////////////////////////////////////////////////////////////////

		br2 = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				
				if (intent.getExtras().getString(MainActivity.FROM) != null
						&& MainActivity.FROM_APPLICATION.equalsIgnoreCase(intent.getExtras().getString(MainActivity.FROM))
						&& intent.getExtras().getString(MainActivity.SOURCE) != null
						&& MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(intent.getExtras().getString(MainActivity.SOURCE))) {
					
					showProgress();
				}
			}
		};
		registerReceiver(br2, new IntentFilter(MainActivity.START_LOAD));
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onDestroy() {

		super.onDestroy();
		
		unregisterReceiver(br1);
		unregisterReceiver(br2);
		stopService();
	}

	public void stopService() {
		stopService(new Intent(NbuHistoryActivity.this, BackgroundService.class));
	}
	
	@Override
	public void onStop() {
		
	    super.onStop();
	    
	    if (connectionToast != null) {
	    	
	    	connectionToast.cancel();
	    	connectionToast = null;
	    }
	}
	
	public void showProgress() {
		
		if (pd == null) {
			
			pd = new ProgressDialog(this);
			pd.setMessage(getString(R.string.loading));
			pd.setCancelable(false);
			
			pd.show();
		}
	}
	
	public void hideProgress() {
		if (pd != null) {
			
			pd.dismiss();
			pd = null;
		}
	}
}