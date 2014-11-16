package dn.ivan.actionbarexample.fragments;

import java.math.BigDecimal;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.Rates;

public class MetalsFragment extends BaseFragment {
	
	static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());		
	static {
		
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(' ');
	}
	
	static DecimalFormat df = new DecimalFormat("###,###,##0.00", dfs);
	static {
		df.setGroupingSize(3);
	}
	
	View rootView;
	
	ArrayList<Object> rates;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.metals_tab_layout, container, false);
		
		ScrollView scrollView = ((ScrollView) rootView.findViewById(R.id.metals_scroll));
		scrollView.setScrollbarFadingEnabled(false);
		
		if (rates != null) {
			setData(rates);
		}
		else {
			((MainActivity)getActivity()).loadRates();
		}
		
		return rootView;
	}
	
	public void setData(ArrayList<Object> rates_) {
		
		ArrayList<Object> checkedMetals = null;
		
		Set<String> metalsSet = null;
		metalsSet = getSetFromPref("selected_metals");
		
		if (metalsSet != null) {
			
			checkedMetals = new ArrayList<Object>();
			
			for (int i = 0; i < rates_.size(); i++) {
				
				Rates item = (Rates) rates_.get(i);
				if (metalsSet.contains(item.char3)) {
					checkedMetals.add(item);
				}
			}
		}
		else {			
			checkedMetals = new ArrayList<Object>(rates_);
		}		

		rates = checkedMetals;
		
		ScrollView scrollView = ((ScrollView) rootView.findViewById(R.id.metals_scroll));
		scrollView.setScrollbarFadingEnabled(true);

		// //////////////////////////////////////////////////////////////////////////////////
		
		LinearLayout list = (LinearLayout) rootView.findViewById(R.id.metals_list);
		list.removeAllViews();
		
		TextView metals_date = (TextView) rootView.findViewById(R.id.metals_date);
		
		LayoutInflater ltInflater = getActivity().getLayoutInflater();
		
		View date_stub = ltInflater.inflate(R.layout.date_stub, null, false);
		list.addView(date_stub);

		// //////////////////////////////////////////////////////////////////////////////////
		
		int scale = Integer.valueOf(getValueFromPref("selected_metals_scale", "0"));
		
		for (int i = 0; i < rates.size(); i++) {
			
			Rates ratesItem = (Rates) rates.get(i);
		    
		    View item = ltInflater.inflate(R.layout.metals_item_layout, null, false);
		    
		    TextView lstItemMetalsLbl = (TextView) item.findViewById(R.id.lstItemMetalsLbl);
		    lstItemMetalsLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName())) + ")"));
		    
		    TextView lstItemMetalsRate = (TextView) item.findViewById(R.id.lstItemMetalsRate);
		    if (scale == 0) {
		    	lstItemMetalsRate.setText(Html.fromHtml(
		    			"<b>" + 
		    			df.format(Double.valueOf(ratesItem.rate)) + 
		    			"</b>" + " " + 
		    			getString(R.string.for_items) + 
		    			" " + "<b>" + 
		    			ratesItem.size + 
		    			"</b>" + " " + 
		    			getString(R.string.ounce)));		    	
		    }
		    else {
		    	lstItemMetalsRate.setText(Html.fromHtml(
		    			"<b>" + 
		    			df.format(new BigDecimal(ratesItem.rate).divide(new BigDecimal("31.1034768"), 2, BigDecimal.ROUND_HALF_UP).doubleValue()) + 
		    			"</b>" + " " + 
		    			getString(R.string.for_items) + 
		    			" " + "<b>" + 
		    			ratesItem.size + 
		    			"</b>" + " " + 
		    			getString(R.string.gram)));
		    }
		    
		    ImageView metals_icon = (ImageView) item.findViewById(R.id.metals_icon);
		    metals_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.char3.trim().toLowerCase(), "drawable", getActivity().getPackageName())));
		    metals_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
		    
		    // /////////////////////////////////////////////////////////////////////
		    
		    ImageView direction = (ImageView) item.findViewById(R.id.rate_metals_direction);
		    if (Double.valueOf(ratesItem.change) > 0) {
				
				direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			else if (Double.valueOf(ratesItem.change) < 0) {
				
				direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
		    
		    // /////////////////////////////////////////////////////////////////////
		    
		    metals_date.setText("КУРСЫ НА " + ratesItem.date);
		    
		    registerForContextMenu(item);
		    
		    list.addView(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		createMetalsSelectionDialog();	    
	}
	
	public void createMetalsSelectionDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.metals_dialog).length];
		
		Set<String> selectedItems = getSetFromPref("selected_metals");
		
		if (selectedItems == null) {
			
			for (int i = 0; i < getResources().getStringArray(R.array.metals_dialog).length; i++)
		        checkedItems[i] = true;
		}
		else {
			
			for (int i = 0; i < getResources().getStringArray(R.array.metals_dialog).length; i++)
		        checkedItems[i] = selectedItems.contains(getResources().getStringArray(R.array.metals_dialog)[i]);
		}	    
	    
	    builder.setTitle("Выбор металлов");
	    builder.setMultiChoiceItems(R.array.metals_dialog, null,
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
								mSelectedItems.add(getResources().getStringArray(R.array.metals_dialog)[i]);
							}
						}
						
						addSet2Pref("selected_metals", new HashSet<String>(mSelectedItems));
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
	
	public void createScaleDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		int currentSelection = Integer.valueOf(getValueFromPref("selected_metals_scale", "0"));
		
		builder.setTitle("Выбор единицы измерения");
	    builder.setSingleChoiceItems(R.array.scale_value, currentSelection,
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						ListView list = ((AlertDialog) dialog).getListView();
						addValue2Pref("selected_metals_scale", String.valueOf(list.getCheckedItemPosition()));
						((MainActivity)getActivity()).loadRates();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
					}
				});
		
		final AlertDialog dialog = builder.create();	    
	    dialog.show();
	}
}