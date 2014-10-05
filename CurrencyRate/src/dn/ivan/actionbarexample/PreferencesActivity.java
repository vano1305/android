package dn.ivan.actionbarexample;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import dn.ivan.actionbarexample.R;

public class PreferencesActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		ActionBar actionBar = getSupportActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.settings);
		actionBar.setDisplayUseLogoEnabled(false);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}