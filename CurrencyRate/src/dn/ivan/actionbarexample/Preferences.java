package dn.ivan.actionbarexample;

import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preferences extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		ActionBar actionBar = getSupportActionBar();
		
		if (Build.VERSION.RELEASE.startsWith("4.") || Build.VERSION.RELEASE.startsWith("3.")) {
			
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
						
		}
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.settings);
		actionBar.setDisplayUseLogoEnabled(false);
		
		//getListView().setBackgroundResource(R.drawable.background_pattern);
	}
}