package dn.ivan.actionbarexample.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.Rates;

public class NbuFragment extends Fragment {

	private ArrayList<Object> rates;

	private View rootView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.nbu_tab_layout, container, false);

		if (rates != null) {
			setData(rates);
		}
		else {
			((MainActivity) getActivity()).loadRates();
		}

		return rootView;
	}

	public void setData(ArrayList<Object> rates_) {
		
		ArrayList<Object> checkedCurrency = null;
		
		Set<String> currencySet = null;
		
		if (Build.VERSION.RELEASE.startsWith("4.") || Build.VERSION.RELEASE.startsWith("3.")) {
			currencySet = getSetFromPref("nbu_currency");			
		}
		else {
			currencySet = null;
		}
		
		if (currencySet != null) {
			
			checkedCurrency = new ArrayList<Object>();
			
			for (int i = 0; i < rates_.size(); i++) {
				
				Rates item = (Rates) rates_.get(i);
				if (currencySet.contains(item.char3)) {
					checkedCurrency.add(item);
				}
			}
		}
		else {			
			checkedCurrency = new ArrayList<Object>(rates_);
		}		

		rates = checkedCurrency;

		ScrollView scrollView = ((ScrollView) rootView.findViewById(R.id.nbu_scroll));
		scrollView.setScrollbarFadingEnabled(true);
		
		LinearLayout list = (LinearLayout) rootView.findViewById(R.id.nbu_list);
		list.removeAllViews();
		
		TextView nbu_date = (TextView) rootView.findViewById(R.id.nbu_date);
		
		LayoutInflater lInflater = getActivity().getLayoutInflater();
		
		View date_stub = lInflater.inflate(R.layout.date_stub, null, false);
		list.addView(date_stub);
		
		for (int i = 0; i < rates.size(); i++) {
			
			Rates ratesItem = (Rates) rates.get(i);			
			
			View item = lInflater.inflate(R.layout.nbu_item_layout, null, false);
			
			// ////////////////////////////////////////////////////////////////////////
			
			ViewHolder vh = new ViewHolder();
			
			vh.lstItemNbuLbl = (TextView) item.findViewById(R.id.lstItemNbuLbl);
			vh.lstItemNbuRate = (TextView) item.findViewById(R.id.lstItemNbuRate);
			vh.country_icon = (ImageView) item.findViewById(R.id.country_icon);
			vh.change = (ImageView) item.findViewById(R.id.nbu_direction);
			
			// ////////////////////////////////////////////////////////////////////////
			
			vh.lstItemNbuLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName())) + ")"));
			vh.lstItemNbuRate.setText(Html.fromHtml("<b>" + ratesItem.rate + "</b>" + " " + getString(R.string.for_items) + " " + "<b>" + ratesItem.size + "</b>" + " " + getString(R.string.items)));

			vh.country_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.char3.trim().toLowerCase(), "drawable", getActivity().getPackageName())));
			vh.country_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

			if (Double.valueOf(ratesItem.change) > 0) {

				vh.change.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			else if (Double.valueOf(ratesItem.change) < 0) {

				vh.change.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			
			nbu_date.setText("����� �� " + ratesItem.date);
			
			registerForContextMenu(item) ;			
			
			list.addView(item);
		}
		
		//new MyTask().execute(rates);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		if (Build.VERSION.RELEASE.startsWith("4.") || Build.VERSION.RELEASE.startsWith("3.")) {
			createDialog();
		}	    
	}
	
	public void createDialog() {
		
		//startActivityForResult(new Intent(getActivity(), NbuCheckActivity.class), 1);
		
	    final ArrayList<String> mSelectedItems = new ArrayList<String>();
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    builder.setTitle(R.string.dialog_title);
		builder.setMultiChoiceItems(R.array.currency_dialog, null,
				new DialogInterface.OnMultiChoiceClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {

						if (isChecked) {
							mSelectedItems.add(getResources().getStringArray(R.array.currency_dialog)[which]);
						}
						else if (mSelectedItems.contains(getResources().getStringArray(R.array.currency_dialog)[which])) {
							mSelectedItems.remove(getResources().getStringArray(R.array.currency_dialog)[which]);
						}
					}
				});
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						addSet2Pref("nbu_currency", new HashSet<String>(mSelectedItems));						
						((MainActivity)getActivity()).loadRates();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int id) {

						//
						//
						//
					}
				});

	    builder.create().show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
	}
	
	@SuppressLint("NewApi")
	protected void addSet2Pref(String prefName, HashSet<String> set) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		Editor ed = shared.edit();
		ed.remove(prefName);
		ed.putStringSet(prefName, set);
		ed.commit();
	}
	
	@SuppressLint("NewApi")
	protected Set<String> getSetFromPref(String prefName) {
		
		SharedPreferences shared = getActivity().getSharedPreferences(prefName, MainActivity.MODE_PRIVATE);
		Set<String> stringSet = shared.getStringSet(prefName, null);
		
		return stringSet;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class ViewHolder {

		public ImageView country_icon;
		public TextView lstItemNbuLbl;
		public TextView lstItemNbuRate;
		public ImageView change;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*class MyTask extends AsyncTask<ArrayList<Object>, Void, Void> {

		ArrayList<Object> rates;

		LayoutInflater lInflater;
		ArrayList<View> verLayout = new ArrayList<View>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			lInflater = getActivity().getLayoutInflater();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			LinearLayout list = (LinearLayout) rootView.findViewById(R.id.nbu_list);
			list.removeAllViews();
			
			TextView nbu_date = (TextView) rootView.findViewById(R.id.nbu_date);
			nbu_date.setText("����� �� " + ((Rates) rates.get(0)).date);
			
			View date_stub = lInflater.inflate(R.layout.date_stub, null, false);
			list.addView(date_stub);
			
			for (int i = 0; i < verLayout.size(); i++) {
				
				registerForContextMenu(verLayout.get(i));
				list.addView(verLayout.get(i));
			}
		}

		@Override
		protected Void doInBackground(ArrayList<Object>... arg0) {

			rates = arg0[0];
			
			for (int i = 0; i < rates.size(); i++) {
				
				Rates ratesItem = (Rates) rates.get(i);			
				
				View item = lInflater.inflate(R.layout.nbu_item_layout, null, false);
				
				// ////////////////////////////////////////////////////////////////////////
				
				ViewHolder vh = new ViewHolder();
				
				vh.lstItemNbuLbl = (TextView) item.findViewById(R.id.lstItemNbuLbl);
				vh.lstItemNbuRate = (TextView) item.findViewById(R.id.lstItemNbuRate);
				vh.country_icon = (ImageView) item.findViewById(R.id.country_icon);
				vh.change = (ImageView) item.findViewById(R.id.nbu_direction);
				
				// ////////////////////////////////////////////////////////////////////////
				
				vh.lstItemNbuLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName())) + ")"));
				vh.lstItemNbuRate.setText(Html.fromHtml("<b>" + ratesItem.rate + "</b>" + " " + getString(R.string.for_items) + " " + "<b>" + ratesItem.size + "</b>" + " " + getString(R.string.items)));

				vh.country_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.char3.trim().toLowerCase(), "drawable", getActivity().getPackageName())));
				vh.country_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

				if (Double.valueOf(ratesItem.change) > 0) {

					vh.change.setImageDrawable(getResources().getDrawable(R.drawable.up));
					vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}
				else if (Double.valueOf(ratesItem.change) < 0) {

					vh.change.setImageDrawable(getResources().getDrawable(R.drawable.down));
					vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
				}
				
				verLayout.add(item);
			}

			return null;
		}
	}*/
}