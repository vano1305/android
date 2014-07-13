package dn.ivan.actionbarexample;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import dn.ivan.actionbarexample.fragments.CommercialFragment;
import dn.ivan.actionbarexample.fragments.MetalsFragment;
import dn.ivan.actionbarexample.fragments.NbuFragment;
import dn.ivan.actionbarexample.fragments.logic.NetworkManager;
import dn.ivan.actionbarexample.fragments.logic.SpinnerNavItem;
import dn.ivan.actionbarexample.fragments.logic.TitleNavigationAdapter;
import dn.ivan.actionbarexample.service.BackgroundService;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	
	public static final int ANIMATION_DURATION = 200;
	
	private ArrayList<SpinnerNavItem> navSpinner;
	private TitleNavigationAdapter adapter;
	
	private static long back_pressed;
	
	private NbuFragment nbuFragment;
	private CommercialFragment currencyFragment;
	private MetalsFragment metalsFragment;
	
	public static final String START_LOAD = "start_load";
	public static final String FINISH_LOAD = "finish_load";
	
	public static final String COMMERCIAL_SOURCE = "commercial";
	public static final String NBU_SOURCE = "nbu";
	public static final String METALS_SOURCE = "metals";
	
	public static final String NBU_FRAGMENT = "nbuFragment";
	public static final String COMMERCIAL_FRAGMENT = "currencyFragment";
	public static final String METALS_FRAGMENT = "metalsFragment";
	
	public static final String SOURCE = "source";
	public static final String RATES = "rates";

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private BroadcastReceiver br1;
	private BroadcastReceiver br2;
	
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(MainActivity.this, "8bb39df7");
		setContentView(R.layout.activity_main);
		
		nbuFragment = (NbuFragment) Fragment.instantiate(this, NbuFragment.class.getName());
		currencyFragment = CommercialFragment.newInstance("USD");
		metalsFragment = (MetalsFragment) Fragment.instantiate(this, MetalsFragment.class.getName());
				
		registerReceiver();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section1), R.drawable.nbu));
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section2), R.drawable.commercial));
        navSpinner.add(new SpinnerNavItem(getString(R.string.title_section3), R.drawable.metals));
         
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);          
        actionBar.setListNavigationCallbacks(adapter, this);        
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
	    	case R.id.refresh:
	    		loadRates();
	    		break;
	    		
	    	case R.id.settings:
	    		startActivity(new Intent(this, Preferences.class));
	    		break;
	    		
	    	default:
	    		break;
	    }
	    return true;
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		
		if (0 == position) {
			
			ft.replace(R.id.container, nbuFragment, NBU_FRAGMENT).commit();
			getActionBar().setIcon(R.drawable.nbu);
			return true;
		}
		else if (1 == position) {
			
			ft.replace(R.id.container, currencyFragment, COMMERCIAL_FRAGMENT).commit();
			getActionBar().setIcon(R.drawable.commercial);
			return true;
		}
		else {	
			
			ft.replace(R.id.container,metalsFragment, METALS_FRAGMENT).commit();
			getActionBar().setIcon(R.drawable.metals);
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
				
				ArrayList<Object> rates = (ArrayList<Object>)intent.getExtras().getSerializable(RATES);				
				setData(rates, intent.getExtras().getString(SOURCE));
				
				// //////////////////////////////////////////////////////////////////////////////////
				
				hideProgress();
			}
		};
		registerReceiver(br1, new IntentFilter(FINISH_LOAD));

		// ////////////////////////////////////////////////////////////////////////////////

		br2 = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				
				showProgress();
			}
		};
		registerReceiver(br2, new IntentFilter(START_LOAD));
	}
	
	public void setData(ArrayList<Object> rates, String source) {
		
		int index = getActionBar().getSelectedNavigationIndex();
		
		if (0 == index && NBU_SOURCE.equalsIgnoreCase(source)) {
			((NbuFragment)getFragmentManager().findFragmentByTag(NBU_FRAGMENT)).setData(rates);
		}
		else if (1 == index && COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
			((CommercialFragment)getFragmentManager().findFragmentByTag(COMMERCIAL_FRAGMENT)).setData(rates);
		}
		else if (2 == index && METALS_SOURCE.equalsIgnoreCase(source)){
			((MetalsFragment)getFragmentManager().findFragmentByTag(METALS_FRAGMENT)).setData(rates);
		}
	}
	
	public void loadRates() {
		
		if (!NetworkManager.checkInternetConnection(this)) {
			
			Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent(MainActivity.this, BackgroundService.class);
		
		int index = getActionBar().getSelectedNavigationIndex();
		
		if (0 == index) {
			intent.putExtra(SOURCE, NBU_SOURCE);
		}
		else if (1 == index) {
			intent.putExtra(SOURCE, COMMERCIAL_SOURCE);
		}
		else {
			intent.putExtra(SOURCE, METALS_SOURCE);
		}
		
		startService(intent);
	}

	public void onDestroy() {

		unregisterReceiver(br1);
		unregisterReceiver(br2);
		stopService();
		super.onDestroy();
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
}