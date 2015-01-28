package dn.ivan.actionbarexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import dn.ivan.actionbarexample.fragments.HistoryFragment;

public class NbuHistoryActivity extends SherlockFragmentActivity {
	
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
            ft.add(CONTENT_VIEW_ID, historyFragment).commit();
        }
				
		ActionBar actionBar = getSupportActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.title_section6);
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