package dn.ivan.actionbarexample.fragments;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.Rates;

public class NbuFragment extends BaseFragment {
	
	static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
	static {
		
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(' ');
	}
	
	static DecimalFormat df = new DecimalFormat("###,###,##0.0000", dfs);
	static {
		df.setGroupingSize(3);
	}

	private ArrayList<Object> rates;

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.nbu_tab_layout, container, false);
		LinearLayout list = (LinearLayout) rootView.findViewById(R.id.nbu_list);
		list.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

		    @Override
		    public void onScrollChanged() {

		        if (currentToast != null) {
		        	
		        	currentToast.cancel();
		        	currentToast = null;
		        }
		    }
		});

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
		currencySet = getSetFromPref("nbu_currency");
		
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
			
			if (getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName()) != 0) {
				vh.lstItemNbuLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName())) + ")"));
			}
			else {
				vh.lstItemNbuLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>"));
			}						
			vh.lstItemNbuRate.setText(Html.fromHtml("<b>" + df.format(Double.valueOf(ratesItem.rate)) + "</b>" + " " + getString(R.string.for_items) + " " + "<b>" + ratesItem.size + "</b>" + " " + getString(R.string.items)));
			
			if (getResources().getIdentifier(ratesItem.char3.trim().toLowerCase(), "drawable", getActivity().getPackageName()) != 0) {
				
				vh.country_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.char3.trim().toLowerCase(), "drawable", getActivity().getPackageName())));
				vh.country_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}

			if (Double.valueOf(ratesItem.change) > 0) {

				vh.change.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.change.setOnClickListener(new DirectionListener("+" + ratesItem.change));
			}
			else if (Double.valueOf(ratesItem.change) < 0) {

				vh.change.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.change.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.change.setOnClickListener(new DirectionListener(ratesItem.change));
			}
			
			nbu_date.setText("ÊÓÐÑÛ ÍÀ " + ratesItem.date);
			
			registerForContextMenu(item);			
			
			list.addView(item);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		createCurrencySelectionDialog();	    
	}
	
	public void createCurrencySelectionDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.currency_dialog).length];
		
		Set<String> selectedItems = getSetFromPref("nbu_currency");
		
		if (selectedItems == null) {
			
			for (int i = 0; i < getResources().getStringArray(R.array.currency_dialog).length; i++)
		        checkedItems[i] = true;
		}
		else {
			
			for (int i = 0; i < getResources().getStringArray(R.array.currency_dialog).length; i++)
		        checkedItems[i] = selectedItems.contains(getResources().getStringArray(R.array.currency_dialog)[i]);
		}	    
	    
	    builder.setTitle(R.string.dialog_title);
	    builder.setMultiChoiceItems(R.array.currency_dialog, null,
				new DialogInterface.OnMultiChoiceClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {

						//
						//
						//
					}
				});
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						final ArrayList<String> mSelectedItems = new ArrayList<String>();
						
						ListView list = ((AlertDialog) dialog).getListView();
						
						for (int i = 0; i < list.getCount(); i++) {
							
							if (list.isItemChecked(i)) {
								mSelectedItems.add(getResources().getStringArray(R.array.currency_dialog)[i]);
							}
						}
						
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
		builder.setNeutralButton(R.string.all,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						//
						//
						//
					}
				});

	    final AlertDialog dialog = builder.create();	    
	    dialog.show();
	    
	    ListView list = dialog.getListView();	    
	    for (int i = 0; i < checkedItems.length; i++) {
	    	list.setItemChecked(i, checkedItems[i]);	   
	    }
	    
	    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ListView list = ((AlertDialog) dialog).getListView();
				
				int selected = 0;
				for (int i = 0; i < list.getCount(); i++) {
					
					if (list.isItemChecked(i)) {
						selected ++;
					}
				}
				
				if (selected == list.getCount()) {
					
					for (int i = 0; i < list.getCount(); ++i)
                        list.setItemChecked(i, false);
				}
				else {
					
					for (int i = 0; i < list.getCount(); ++i)
                        list.setItemChecked(i, true);
				}				
			}
		});
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class ViewHolder {

		public ImageView country_icon;
		public TextView lstItemNbuLbl;
		public TextView lstItemNbuRate;
		public ImageView change;
	}
}