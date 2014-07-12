package dn.ivan.actionbarexample;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		ActionBar actionBar = getActionBar();		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.settings);
		actionBar.setDisplayUseLogoEnabled(false);
		
		//getListView().setBackgroundResource(R.drawable.background_pattern);
	}
}