package dn.ivan.actionbarexample;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;

import dn.ivan.actionbarexample.fragments.CommercialFragment;
import dn.ivan.actionbarexample.fragments.HistoryFragment;
import dn.ivan.actionbarexample.fragments.MetalsFragment;
import dn.ivan.actionbarexample.fragments.NbuFragment;
import dn.ivan.actionbarexample.logic.NetworkManager;
import dn.ivan.actionbarexample.logic.SpinnerNavItem;
import dn.ivan.actionbarexample.logic.TitleNavigationAdapter;
import dn.ivan.actionbarexample.service.BackgroundService;
import dn.ivan.actionbarexample.service.HistoryService;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
	
	private ArrayList<SpinnerNavItem> navSpinner;
	private TitleNavigationAdapter adapter;
	
	private static long back_pressed;
	
	private NbuFragment nbuFragment;
	private CommercialFragment currencyFragment;
	private MetalsFragment metalsFragment;
	private HistoryFragment historyFragment;
	
	public static final String START_LOAD = "start_load";
	public static final String FINISH_LOAD = "finish_load";
	
	public static final String COMMERCIAL_SOURCE = "commercial";
	public static final String NBU_SOURCE = "nbu";
	public static final String METALS_SOURCE = "metals";
	
	public static final String FROM_SERVICE_HISTORY = "service_history";
	public static final String FROM_APPLICATION = "application";
	public static final String UPDATE_HISTORY = "update_history";
	
	public static final String NBU_FRAGMENT = "nbuFragment";
	public static final String COMMERCIAL_FRAGMENT = "currencyFragment";
	public static final String METALS_FRAGMENT = "metalsFragment";
	public static final String HISTORY_FRAGMENT = "historyFragment";
	
	public static final String SOURCE = "source";
	public static final String RATES = "rates";

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private BroadcastReceiver br1;
	private BroadcastReceiver br2;
	
	private ProgressDialog pd;
	
	private Menu menu;
	private Animation rotation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(MainActivity.this, "8bb39df7");
		setContentView(R.layout.activity_main);
		
		nbuFragment = (NbuFragment) Fragment.instantiate(this, NbuFragment.class.getName());
		currencyFragment = CommercialFragment.newInstance("USD");
		metalsFragment = (MetalsFragment) Fragment.instantiate(this, MetalsFragment.class.getName());
		historyFragment = (HistoryFragment) Fragment.instantiate(this, HistoryFragment.class.getName());
				
		registerReceiver();
		if (!isServiceRunning(HistoryService.class)) {
			startService(new Intent(MainActivity.this, HistoryService.class));
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section1), R.drawable.nbu));
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section2), R.drawable.commercial));
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section3), R.drawable.metals));
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section4), R.drawable.history));
         
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);          
        actionBar.setListNavigationCallbacks(adapter, this);
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.main, menu);
		
		this.menu = menu;
		rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
	    	case R.id.refresh:
	    		loadRates();
	    		break;
	    		
	    	/*case R.id.settings:
	    		startActivity(new Intent(this, Preferences.class));
	    		break;*/
	    		
	    	default:
	    		break;
	    }
	    return true;
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onNavigationItemSelected(int position, long id) {		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		
		if (0 == position) {
			
			ft.replace(R.id.container, nbuFragment, NBU_FRAGMENT).commit();
			getSupportActionBar().setIcon(R.drawable.nbu);
			if (menu != null) {
				menu.findItem(R.id.refresh).setVisible(true);
			}			
			return true;
		}
		else if (1 == position) {
			
			ft.replace(R.id.container, currencyFragment, COMMERCIAL_FRAGMENT).commit();
			getSupportActionBar().setIcon(R.drawable.commercial);
			if (menu != null) {
				menu.findItem(R.id.refresh).setVisible(true);
			}
			return true;
		}
		else if (2 == position) {	
			
			ft.replace(R.id.container, metalsFragment, METALS_FRAGMENT).commit();
			getSupportActionBar().setIcon(R.drawable.metals);
			if (menu != null) {
				menu.findItem(R.id.refresh).setVisible(true);
			}
			return true;
		}
		else {
			
			ft.replace(R.id.container, historyFragment, HISTORY_FRAGMENT).commit();
			getSupportActionBar().setIcon(R.drawable.history);
			if (menu != null) {
				menu.findItem(R.id.refresh).setVisible(false);
			}
			return true;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void registerReceiver() {
		
		br1 = new BroadcastReceiver() {

			@SuppressWarnings("unchecked")
			public void onReceive(Context context, Intent intent) {
				
				if (intent.getExtras().getString("error") == null) {
					
					ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(RATES);
					setData(rates, intent.getExtras().getString(SOURCE));
				}				
				
				// //////////////////////////////////////////////////////////////////////////////////
				
				MenuItem menuItem = menu.findItem(R.id.refresh);
				menuItem.getActionView().clearAnimation();
				menuItem.setActionView(null);
			}
		};
		registerReceiver(br1, new IntentFilter(FINISH_LOAD));

		// ////////////////////////////////////////////////////////////////////////////////

		br2 = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
				
			    MenuItem menuItem = menu.findItem(R.id.refresh);
				menuItem.setActionView(iv);
				menuItem.getActionView().startAnimation(rotation);
			}
		};
		registerReceiver(br2, new IntentFilter(START_LOAD));
	}
	
	public void setData(ArrayList<Object> rates, String source) {
		
		int index = getSupportActionBar().getSelectedNavigationIndex();
				
		if (0 == index && NBU_SOURCE.equalsIgnoreCase(source)) {
			((NbuFragment)getSupportFragmentManager().findFragmentByTag(NBU_FRAGMENT)).setData(rates);
		}
		else if (1 == index && COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
			((CommercialFragment)getSupportFragmentManager().findFragmentByTag(COMMERCIAL_FRAGMENT)).setData(rates);
		}
		else if (2 == index && METALS_SOURCE.equalsIgnoreCase(source)){
			((MetalsFragment)getSupportFragmentManager().findFragmentByTag(METALS_FRAGMENT)).setData(rates);
		}
	}
	
	public void loadRates() {
		
		if (!NetworkManager.checkInternetConnection(this)) {
			
			Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent(MainActivity.this, BackgroundService.class);
		
		int index = getSupportActionBar().getSelectedNavigationIndex();
				
		if (0 == index) {
			intent.putExtra(SOURCE, NBU_SOURCE);
		}
		else if (1 == index) {
			intent.putExtra(SOURCE, COMMERCIAL_SOURCE);
		}
		else if (2 == index) {
			intent.putExtra(SOURCE, METALS_SOURCE);
		}
		
		startService(intent);
	}

	public void onDestroy() {

		super.onDestroy();
		
		unregisterReceiver(br1);
		unregisterReceiver(br2);
		stopService();
	}

	// //////////////////////////////////////////////////

	public void stopService() {
		stopService(new Intent(MainActivity.this, BackgroundService.class));
	}
	
	public void showProgress() {
		
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.loading));
		pd.setCancelable(false);
		
		pd.show();
	}
	
	public void hideProgress() {
		pd.dismiss();
	}
	
	@Override
	public void onBackPressed() {
		
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		}
		else {
			Toast.makeText(getBaseContext(), getString(R.string.exit), Toast.LENGTH_SHORT).show();
		}

		back_pressed = System.currentTimeMillis();
	}
	
	@Override
	  public void onStart() {
		
	    super.onStart();
	   
	    EasyTracker.getInstance(this).activityStart(this);
	  }

	@Override
	  public void onStop() {
		
	    super.onStop();
	    
	    EasyTracker.getInstance(this).activityStop(this);
	  }
	
	private boolean isServiceRunning(Class<?> serviceClass) {
		
		boolean result = false;

		try {

			ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {				
				if (serviceClass.getName().trim().equalsIgnoreCase(service.service.getClassName().trim())) {					
					result = true;
				}
			}
		}
		catch (Exception e) {
			Log.v("MainActivity", e.toString());
		}

		return result;
	}
}