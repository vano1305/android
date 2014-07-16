package dn.ivan.actionbarexample;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class NbuCheckActivity extends SherlockListActivity {
	
	ArrayList<String> mSelectedItems = new ArrayList<String>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nbu_check_layout);        
        
        // ////////////////////////////////////////////////////////////
        
        ActionBar actionBar = getSupportActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("");
		actionBar.setDisplayUseLogoEnabled(false);
        
        // ////////////////////////////////////////////////////////////
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.currency_dialog));
        
        getListView().setAdapter(adapter);                
		
        OnClickListener clickListener = new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				
				CheckBox chk = (CheckBox) v;
				int itemCount = getListView().getCount();
				for(int i=0; i < itemCount; i++) {
					getListView().setItemChecked(i, chk.isChecked());
				}
			}
		};		
		
		OnItemClickListener itemClickListener = new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				CheckBox chk = (CheckBox) findViewById(R.id.chkAll);
				int checkedItemCount = getCheckedItemCount();								
				
				if(getListView().getCount() == checkedItemCount) {
					chk.setChecked(true);
				}
				else {
					chk.setChecked(false);
				}
				
				if (chk.isChecked()) {
					mSelectedItems.add(getResources().getStringArray(R.array.currency_dialog)[arg2]);
				}
				else if (mSelectedItems.contains(arg2)) {
					mSelectedItems.remove(getResources().getStringArray(R.array.currency_dialog)[arg2]);
				}
			}
		};		
        
		CheckBox chkAll =  (CheckBox) findViewById(R.id.chkAll);  
        
        chkAll.setOnClickListener(clickListener);        
        getListView().setOnItemClickListener(itemClickListener);
    }
    
    private int getCheckedItemCount() {
    	
    	int cnt = 0;
    	SparseBooleanArray positions = getListView().getCheckedItemPositions();
    	int itemCount = getListView().getCount();
    	
    	for(int i=0; i < itemCount; i++) {
    		
    		if(positions.get(i)) {
    			cnt ++;
    		}
    	}
    	
    	return cnt;
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