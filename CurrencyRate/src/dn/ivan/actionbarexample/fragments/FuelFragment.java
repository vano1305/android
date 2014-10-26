package dn.ivan.actionbarexample.fragments;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.FuelItem;

public class FuelFragment extends BaseFragment {
	
	ArrayList<FuelItem> notSortRates;
	
	View mainLayout = null;
    View averageRatesItem = null;
	
    String regionCode = "";
	String fuelCode = "";
	
	boolean isLoadRunning = false;
	
	public static FuelFragment newInstance(String regionCode, String fuelCode) {
		
		FuelFragment currency = new FuelFragment();		
	    return currency;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public View onCreateView(LayoutInflater ltInflater, ViewGroup container, Bundle savedInstanceState) {
		
		mainLayout = ltInflater.inflate(R.layout.fuel_tab_layout, container, false);
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.fuel_list);
		list.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

		    @Override
		    public void onScrollChanged() {

		        if (currentToast != null) {
		        	
		        	currentToast.cancel();
		        	currentToast = null;
		        }
		    }
		});
				
		createAverageRatesItem();
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		Spinner regionSpinner = (Spinner) mainLayout.findViewById(R.id.region_code_spinner);		
		ArrayAdapter<CharSequence> regionSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region_spinner_value, R.layout.currency_spinner_pattern);
		regionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		regionSpinner.setAdapter(regionSpinnerAdapter);
		regionSpinner.setSelection(Integer.valueOf(getValueFromPref("region_code", "9")));
		regionSpinner.setOnItemSelectedListener(new RegionSpinnerListener());
		
		Spinner fuelSpinner = (Spinner) mainLayout.findViewById(R.id.fuel_code_spinner);		
		ArrayAdapter<CharSequence> fuelSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.fuel_spinner_value, R.layout.currency_spinner_pattern);
		fuelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fuelSpinner.setAdapter(fuelSpinnerAdapter);
		fuelSpinner.setSelection(Integer.valueOf(getValueFromPref("fuel_code", "2")));
		fuelSpinner.setOnItemSelectedListener(new FuelSpinnerListener());
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		if (notSortRates != null) {
			setData(notSortRates);
		}
		
		return mainLayout;
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void createAverageRatesItem() {
		
		averageRatesItem = getActivity().getLayoutInflater().inflate(R.layout.fuel_average_item_layout, null, false);
		
		TextView fuel_average_txt_lbl = (TextView) averageRatesItem.findViewById(R.id.fuel_average_txt_lbl);
		fuel_average_txt_lbl.setText("Стоимость на ...");
	    
	    TextView fuel_average_txt = (TextView) averageRatesItem.findViewById(R.id.fuel_average_txt);
	    fuel_average_txt.setText("0.0");
	    
	    ((LinearLayout) mainLayout.findViewById(R.id.average_fuel_rates)).addView(averageRatesItem);
	}
	
	@SuppressWarnings("unchecked")
	public void setData(Object rates_) {
		
		notSortRates = (ArrayList<FuelItem>) rates_;
						
		// //////////////////////////////////////////////////////////////////////////////////
		
		ArrayList<FuelItem> rates = new ArrayList<FuelItem>();
		for (int i = 0; i < notSortRates.size(); i++) {
			
			if (!"".equalsIgnoreCase(notSortRates.get(i).code)) {
				rates.add(notSortRates.get(i));
			}
		}
		
		// ///////////////////////////////////////////////////////////////////////////
		
		ArrayList<FuelItem> checkedFuelStation = null;
		
		Set<String> fuelStationSet = null;		
		fuelStationSet = getSetFromPref("selected_fuel_station");
		
		if (fuelStationSet != null) {
			
			checkedFuelStation = new ArrayList<FuelItem>();
			
			for (int i = 0; i < rates.size(); i++) {
				
				FuelItem item = (FuelItem) rates.get(i);
				if (fuelStationSet.contains(item.code)) {
					checkedFuelStation.add(item);
				}
			}
		}
		else {			
			checkedFuelStation = new ArrayList<FuelItem>(rates);
		}
		
		rates = checkedFuelStation;
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		ScrollView scrollView = ((ScrollView) mainLayout.findViewById(R.id.fuel_scroll));
		scrollView.setScrollbarFadingEnabled(true);
		
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.fuel_list);
		list.removeAllViews();
		
		LayoutInflater lInflater = getActivity().getLayoutInflater();
		
		View stub = lInflater.inflate(R.layout.commercial_triangle_stub, null, false);
		list.addView(stub);
		
		TextView current_date = (TextView) averageRatesItem.findViewById(R.id.fuel_average_txt_lbl);
		current_date.setText("СТОИМОСТЬ НА " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
		
		double totalPrice = 0.0;
		double totalDelta = 0.0;
		int count = 0;
		
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		HashMap<Integer, Double> not_sorted_map = new HashMap<Integer, Double>();
        ValueComparator bvc =  new ValueComparator(not_sorted_map);
        TreeMap<Integer, Double> sorted_map = new TreeMap<Integer, Double>(bvc);
        
        for (int i = 0; i < rates.size(); i++) {
			
			FuelItem ratesItem = (FuelItem) rates.get(i);
			
			if ("a_80".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_80)) {				
				not_sorted_map.put(i, Double.valueOf(ratesItem.a_80));
			}
			if ("a_92".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_92)) {
				not_sorted_map.put(i, Double.valueOf(ratesItem.a_92));
			}
			if ("a_95".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_95)) {
				not_sorted_map.put(i, Double.valueOf(ratesItem.a_95));
			}
			if ("dt".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.dt)){
				not_sorted_map.put(i, Double.valueOf(ratesItem.dt));
			}
        }

        sorted_map.putAll(not_sorted_map);
		
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
        Integer[] mapKeys = new Integer[sorted_map.size()];
        int pos = 0;
        for (Integer key : sorted_map.keySet()) {
            mapKeys[pos++] = key;
        }
        
       	for (int i = 0; i < mapKeys.length; i++) {
			
			FuelItem ratesItem = (FuelItem) rates.get(mapKeys[i]);
			
			View item = lInflater.inflate(R.layout.fuel_item_layout, null, false);
			
			// ////////////////////////////////////////////////////////////////////
			
			ViewHolder vh = new ViewHolder();
			
			vh.lstItemFuelLbl = (TextView) item.findViewById(R.id.lstItemFuelLbl);
			vh.lstItemFuelRate = (TextView) item.findViewById(R.id.lstItemFuelRate);
			vh.fuel_station_icon = (ImageView) item.findViewById(R.id.fuel_station_icon);
			vh.fuel_delta_direction = (ImageView) item.findViewById(R.id.fuel_delta_direction);
			
			if (getResources().getIdentifier(ratesItem.code, "string", getActivity().getPackageName()) != 0) {
				vh.lstItemFuelLbl.setText(Html.fromHtml("<b>" + getResources().getString(getResources().getIdentifier(ratesItem.code, "string", getActivity().getPackageName())) + "</b>"));
			}
			else {
				vh.lstItemFuelLbl.setText(Html.fromHtml("<b>" + ratesItem.name + "</b>"));
			}
			
			if ("a_80".equalsIgnoreCase(fuelCode)) {				
				vh.lstItemFuelRate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.a_80)? "0": ratesItem.a_80).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			}
			else if ("a_92".equalsIgnoreCase(fuelCode)) {
				vh.lstItemFuelRate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.a_92)? "0": ratesItem.a_92).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));				
			}
			else if ("a_95".equalsIgnoreCase(fuelCode)) {
				vh.lstItemFuelRate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.a_95)? "0": ratesItem.a_95).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));				
			}
			else if ("dt".equalsIgnoreCase(fuelCode)) {
				vh.lstItemFuelRate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.dt)? "0": ratesItem.dt).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			}
			
			try {
				vh.fuel_station_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.code, "drawable", getActivity().getPackageName())));
				vh.fuel_station_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			catch (Exception e) {}
			
			// ///////////////////////////////////////////////////////////////////////////////////////////////
			
			double delta = 0;			
			if ("a_80".equalsIgnoreCase(fuelCode)) {		
				delta = "".equalsIgnoreCase(ratesItem.a_80_delta)? 0: Double.valueOf(ratesItem.a_80_delta);						
			}
			else if ("a_92".equalsIgnoreCase(fuelCode)) {
				delta = "".equalsIgnoreCase(ratesItem.a_92_delta)? 0: Double.valueOf(ratesItem.a_92_delta);
			}
			else if ("a_95".equalsIgnoreCase(fuelCode)) {
				delta = "".equalsIgnoreCase(ratesItem.a_95_delta)? 0: Double.valueOf(ratesItem.a_95_delta);								
			}
			else if ("dt".equalsIgnoreCase(fuelCode)) {
				delta = "".equalsIgnoreCase(ratesItem.dt_delta)? 0: Double.valueOf(ratesItem.dt_delta);
			}
			
			if (delta > 0) {
				vh.fuel_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.fuel_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.fuel_delta_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else if (delta < 0) {
				vh.fuel_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.fuel_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.fuel_delta_direction.setOnClickListener(new DirectionListener(new BigDecimal(delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				vh.fuel_delta_direction.setImageDrawable(null);
			}
			
			// ///////////////////////////////////////////////////////////////////////////////////////////////
			
			registerForContextMenu(item);
			
			list.addView(item);
			
			// //////////////////////////////////////////////////////////////////////////////////////
			
			if ("a_80".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_80)) {				
				totalPrice = new BigDecimal(totalPrice).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_80)? "0": ratesItem.a_80)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				totalDelta = new BigDecimal(totalDelta).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_80_delta)? "0": ratesItem.a_80_delta)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				count++;
			}
			else if ("a_92".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_92)) {
				totalPrice = new BigDecimal(totalPrice).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_92)? "0": ratesItem.a_92)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				totalDelta = new BigDecimal(totalDelta).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_92_delta)? "0": ratesItem.a_92_delta)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				count++;
			}
			else if ("a_95".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.a_95)) {
				totalPrice = new BigDecimal(totalPrice).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_95)? "0": ratesItem.a_95)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				totalDelta = new BigDecimal(totalDelta).add(new BigDecimal("".equalsIgnoreCase(ratesItem.a_95_delta)? "0": ratesItem.a_95_delta)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				count++;
			}
			else if ("dt".equalsIgnoreCase(fuelCode) && !"".equalsIgnoreCase(ratesItem.dt)) {
				totalPrice = new BigDecimal(totalPrice).add(new BigDecimal("".equalsIgnoreCase(ratesItem.dt)? "0": ratesItem.dt)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				totalDelta = new BigDecimal(totalDelta).add(new BigDecimal("".equalsIgnoreCase(ratesItem.dt_delta)? "0": ratesItem.dt_delta)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				count++;
			}
		}
		
		// /////////////////////////////////////////////////////////////////////////////////
		
		TextView fuel_average_txt = (TextView) averageRatesItem.findViewById(R.id.fuel_average_txt);
		fuel_average_txt.setText(Html.fromHtml("<b>" + new BigDecimal(count == 0? 0: (totalPrice / count)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
		
		ImageView average_fuel_direction = (ImageView) averageRatesItem.findViewById(R.id.average_fuel_direction);
		if (count != 0 && new BigDecimal(totalDelta / count).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() > 0) {
			average_fuel_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
			average_fuel_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			average_fuel_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(totalDelta / count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
		}
		else if (count != 0 && new BigDecimal(totalDelta / count).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() < 0) {
			average_fuel_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
			average_fuel_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			average_fuel_direction.setOnClickListener(new DirectionListener(new BigDecimal(totalDelta / count).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
		}
		else {
			average_fuel_direction.setImageDrawable(null);
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		createFuelStationsSelectionDialog();
	}
	
	public void createFuelStationsSelectionDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.fuel_station_key).length];
		
		Set<String> selectedItems = getSetFromPref("selected_fuel_station");
		
		if (selectedItems == null) {
			
			for (int i = 0; i < getResources().getStringArray(R.array.fuel_station_key).length; i++)
		        checkedItems[i] = true;
		}
		else {
			
			for (int i = 0; i < getResources().getStringArray(R.array.fuel_station_key).length; i++)
		        checkedItems[i] = selectedItems.contains(getResources().getStringArray(R.array.fuel_station_key)[i]);
		}	    
	    
	    builder.setTitle("Выбор заправок");
	    builder.setMultiChoiceItems(R.array.fuel_station_value, null,
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
								mSelectedItems.add(getResources().getStringArray(R.array.fuel_station_key)[i]);
							}
						}
						
						addSet2Pref("selected_fuel_station", new HashSet<String>(mSelectedItems));
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
	
	public String getRegionCode() {
		return regionCode;
	}
	
	public void setLoadStatusComplete() {
		isLoadRunning = false;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class ViewHolder {
		
		public TextView lstItemFuelLbl;
		public TextView lstItemFuelRate;
		
		public ImageView fuel_station_icon;
		public ImageView fuel_delta_direction;
	}
	
	class RegionSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			String newRegionCode = getResources().getStringArray(R.array.region_spinner_key)[((Spinner) mainLayout.findViewById(R.id.region_code_spinner)).getSelectedItemPosition()];
			boolean isEqualsRegion = regionCode.equalsIgnoreCase(newRegionCode);
						
			regionCode = getResources().getStringArray(R.array.region_spinner_key)[((Spinner) mainLayout.findViewById(R.id.region_code_spinner)).getSelectedItemPosition()];
			addValue2Pref("region_code", String.valueOf(((Spinner) mainLayout.findViewById(R.id.region_code_spinner)).getSelectedItemPosition()));
			
			if (!isLoadRunning && !isEqualsRegion) {
				isLoadRunning = true;
				((MainActivity)getActivity()).loadRates();
			}			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
						
		}		
	}
	
	class FuelSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
			fuelCode = getResources().getStringArray(R.array.fuel_spinner_key)[((Spinner) mainLayout.findViewById(R.id.fuel_code_spinner)).getSelectedItemPosition()];
			addValue2Pref("fuel_code", String.valueOf(((Spinner) mainLayout.findViewById(R.id.fuel_code_spinner)).getSelectedItemPosition()));
						
			if (notSortRates == null && !"".equalsIgnoreCase(regionCode) && !isLoadRunning) {
				
				isLoadRunning = true;
				((MainActivity)getActivity()).loadRates();
			}
			else if (notSortRates != null) {
				setData(notSortRates);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
						
		}		
	}
	
	class ValueComparator implements Comparator<Integer> {

	    Map<Integer, Double> base;
	    
	    public ValueComparator(Map<Integer, Double> base) {
	        this.base = base;
	    }

	    public int compare(Integer a, Integer b) {
	    	
	        if (base.get(a) <= base.get(b)) {
	            return -1;
	        }
	        else {
	            return 1;
	        }
	    }
	}
}