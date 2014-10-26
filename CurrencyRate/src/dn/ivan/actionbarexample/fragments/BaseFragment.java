package dn.ivan.actionbarexample.fragments;

import java.util.HashSet;
import java.util.Set;

import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment {
	
	Toast currentToast = null;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	protected void addSet2Pref(String prefName, HashSet<String> set) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		Editor ed = shared.edit();
		ed.remove(prefName);
		ed.putStringSet(prefName, set);
		ed.commit();
	}
	
	protected Set<String> getSetFromPref(String prefName) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		Set<String> stringSet = shared.getStringSet(prefName, null);
		
		return stringSet;
	}
	
	public class DirectionListener implements OnClickListener {
		
		String change = "";
		
		public DirectionListener(String change_) {
			change = change_;		
		}
		
		@Override
		public void onClick(View v) {
			
			int[] location = new int[2];			
			v.getLocationOnScreen(location);
			
			LayoutInflater inflater = getActivity().getLayoutInflater();
			
	        View toastRoot = inflater.inflate(R.layout.toast, null);
	        if (Double.valueOf(change) > 0) {
	        	toastRoot.setBackgroundResource(R.drawable.shape_toast_up);
	        }
	        else {
	        	toastRoot.setBackgroundResource(R.drawable.shape_toast_down);
	        }
	        
	        ((TextView)toastRoot.findViewById(R.id.toast_text)).setText(change);
	        
	        if (currentToast != null) {
	        	
	        	currentToast.cancel();
	        	currentToast = null;
	        }
	        
	        currentToast = new Toast(getActivity());
	        currentToast.setView(toastRoot);
	        currentToast.setDuration(Toast.LENGTH_LONG);
	        currentToast.setGravity(Gravity.TOP|Gravity.LEFT,
					location[0] - dpToPx(20),
					location[1] - dpToPx(57));
			
	        currentToast.show();
		}		
	}

	public int dpToPx(int dp) {
		
	    DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
	    int px = Math.round(dp * displayMetrics.density);
	    return px;
	}
	
	@Override
	public void onStop() {
		
	    super.onStop();
	    
	    if (currentToast != null) {
	    	
	    	currentToast.cancel();
	    	currentToast = null;
	    }
	}
	
	protected void addValue2Pref(String prefName, String value) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		Editor ed = shared.edit();
		ed.remove(prefName);
		ed.putString(prefName, value);
		ed.commit();
	}
	
	protected String getValueFromPref(String prefName, String defValue) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		String value = shared.getString(prefName, defValue);
		
		return value;
	}
}
