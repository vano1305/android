package dn.ivan.actionbarexample.fragments;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
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
import dn.ivan.actionbarexample.logic.BlackMarketItem;
import dn.ivan.actionbarexample.logic.BlackMarketItemForView;
import dn.ivan.actionbarexample.logic.Rates;

public class BlackMarketFragment extends BaseFragment {
	
	ArrayList<BlackMarketItem> notSortRates;
	
	View mainLayout = null;
    	
    String cityCode = "";
	
	boolean isLoadRunning = false;
	
	public static BlackMarketFragment newInstance(String regionCode) {
		
		BlackMarketFragment blackMarketFragment = new BlackMarketFragment();		
	    return blackMarketFragment;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public View onCreateView(LayoutInflater ltInflater, ViewGroup container, Bundle savedInstanceState) {
		
		mainLayout = ltInflater.inflate(R.layout.black_market_tab_layout, container, false);
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.black_market_list);
		list.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

		    @Override
		    public void onScrollChanged() {

		        if (currentToast != null) {
		        	
		        	currentToast.cancel();
		        	currentToast = null;
		        }
		    }
		});
				
		// //////////////////////////////////////////////////////////////////////////////////
		
		Spinner citySpinner = (Spinner) mainLayout.findViewById(R.id.city_code_spinner);		
		ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.city_spinner_value, R.layout.currency_spinner_pattern);
		citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		citySpinner.setAdapter(citySpinnerAdapter);
		citySpinner.setSelection(Integer.valueOf(getValueFromPref("city_code", "1")));
		citySpinner.setOnItemSelectedListener(new CitySpinnerListener());
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		if (notSortRates != null) {
			setData(notSortRates);
		}
		
		return mainLayout;
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public void setData(Object rates_) {
		
		notSortRates = (ArrayList<BlackMarketItem>) rates_;
		
		// ////////////////////////////////////////////////////////
		
		ArrayList<BlackMarketItemForView> rates = new ArrayList<BlackMarketItemForView>();
		
		BlackMarketItemForView usd = new BlackMarketItemForView();
		BlackMarketItemForView eur = new BlackMarketItemForView();
		BlackMarketItemForView rub = new BlackMarketItemForView();
		
		for (int i = 0; i < notSortRates.size(); i++) {
			
			BlackMarketItem item = notSortRates.get(i);
			
			if ("USD".equalsIgnoreCase(item.currencyCode)) {
				
				usd.currencyCode = item.currencyCode;
				usd.cityCode = item.cityCode;
				usd.date = item.date;
				
				if ("1".equalsIgnoreCase(item.opCode)) {
					usd.rate_buy = item.rate;
					usd.rate_buy_delta = item.rate_delta;
				}
				else {
					usd.rate_sale = item.rate;
					usd.rate_sale_delta = item.rate_delta;
				}
			}
			if ("EUR".equalsIgnoreCase(item.currencyCode)) {
				
				eur.currencyCode = item.currencyCode;
				eur.cityCode = item.cityCode;
				eur.date = item.date;
				
				if ("1".equalsIgnoreCase(item.opCode)) {
					eur.rate_buy = item.rate;
					eur.rate_buy_delta = item.rate_delta;
				}
				else {
					eur.rate_sale = item.rate;
					eur.rate_sale_delta = item.rate_delta;
				}
			}
			if ("RUB".equalsIgnoreCase(item.currencyCode)) {
				
				rub.currencyCode = item.currencyCode;
				rub.cityCode = item.cityCode;
				rub.date = item.date;
	
				if ("1".equalsIgnoreCase(item.opCode)) {
					rub.rate_buy = item.rate;
					rub.rate_buy_delta = item.rate_delta;
				}
				else {
					rub.rate_sale = item.rate;
					rub.rate_sale_delta = item.rate_delta;
				}
			}			
		}
		
		Set<String> currencySet = null;		
		currencySet = getSetFromPref("black_market_currency_selection");
		
		if (currencySet != null) {
			
			if (currencySet.contains("USD")) {
				rates.add(usd);
			}
			if (currencySet.contains("EUR")) {
				rates.add(eur);
			}
			if (currencySet.contains("RUB")) {
				rates.add(rub);
			}
		}
		else {			
			
			rates.add(usd);
			rates.add(eur);
			rates.add(rub);
		}
		
		// ////////////////////////////////////////////////////////
						
		ScrollView scrollView = ((ScrollView) mainLayout.findViewById(R.id.black_market_scroll));
		scrollView.setScrollbarFadingEnabled(true);
		
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.black_market_list);
		list.removeAllViews();
		
		LayoutInflater lInflater = getActivity().getLayoutInflater();
		
		View date_stub = lInflater.inflate(R.layout.date_stub, null, false);
		list.addView(date_stub);
		
		TextView current_date = (TextView) mainLayout.findViewById(R.id.black_market_date);
		current_date.setText(" ”–—€ Õ¿ ...");
		
		View black_market_head = lInflater.inflate(R.layout.black_market_head, null, false);
		list.addView(black_market_head);
		
		TextView averageBuyTxt = (TextView) black_market_head.findViewById(R.id.black_market_buy_lbl);
	    averageBuyTxt.setText(Html.fromHtml("<b>" + getString(R.string.buy) + "</b>"));
	    
	    TextView averageSellTxt = (TextView) black_market_head.findViewById(R.id.black_market_sell_lbl);
	    averageSellTxt.setText(Html.fromHtml("<b>" + getString(R.string.sell) + "</b>"));
		
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		for (int i = 0; i < rates.size(); i++) {
			
			BlackMarketItemForView ratesItem = (BlackMarketItemForView) rates.get(i);
			
			current_date.setText(" ”–—€ Õ¿ " + ratesItem.date);
			
			View item = lInflater.inflate(R.layout.black_market_item_layout, null, false);
			
			// ////////////////////////////////////////////////////////////////////
			
			ViewHolder vh = new ViewHolder();
			
			vh.currency_icon = (ImageView) item.findViewById(R.id.black_market_currency_icon);
			
			vh.lbl = (TextView) item.findViewById(R.id.black_market_currency_txt);
			
			vh.buy_rate = (TextView) item.findViewById(R.id.black_market_buy_txt);
			vh.sale_rate = (TextView) item.findViewById(R.id.black_market_sell_txt);
			vh.buy_delta_direction = (ImageView) item.findViewById(R.id.black_market_buy_direction_img);
			vh.sale_delta_direction = (ImageView) item.findViewById(R.id.black_market_sell_direction_img);
			
			if (getResources().getIdentifier(ratesItem.currencyCode.toUpperCase(), "string", getActivity().getPackageName()) != 0) {
				vh.lbl.setText(Html.fromHtml("<b>" + ratesItem.currencyCode.toUpperCase() + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.currencyCode.toUpperCase(), "string", getActivity().getPackageName())) + ")"));
			}
			else {
				vh.lbl.setText(Html.fromHtml("<b>" + ratesItem.currencyCode + "</b>"));
			}
			
			if (getResources().getIdentifier(ratesItem.currencyCode.trim().toLowerCase(), "drawable", getActivity().getPackageName()) != 0) {
				
				vh.currency_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.currencyCode.trim().toLowerCase(), "drawable", getActivity().getPackageName())));
				vh.currency_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			
			vh.buy_rate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.rate_buy)? "0": ratesItem.rate_buy).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			vh.sale_rate.setText(Html.fromHtml("<b>" + new BigDecimal("".equalsIgnoreCase(ratesItem.rate_sale)? "0": ratesItem.rate_sale).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			
			// ///////////////////////////////////////////////////////////////////////////////////////////////
			
			double buy_delta = "".equalsIgnoreCase(ratesItem.rate_buy_delta)? 0: Double.valueOf(ratesItem.rate_buy_delta);	
			
			if (buy_delta > 0) {
				vh.buy_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.buy_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.buy_delta_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(buy_delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else if (buy_delta < 0) {
				vh.buy_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.buy_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.buy_delta_direction.setOnClickListener(new DirectionListener(new BigDecimal(buy_delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				vh.buy_delta_direction.setImageDrawable(null);
			}
			
			// ////////////////////////////////////////////////////////////////////////////////////////////////
			
			double sale_delta = "".equalsIgnoreCase(ratesItem.rate_sale_delta)? 0: Double.valueOf(ratesItem.rate_sale_delta);
			
			if (sale_delta > 0) {
				vh.sale_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.sale_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.sale_delta_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(sale_delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else if (sale_delta < 0) {
				vh.sale_delta_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.sale_delta_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				vh.sale_delta_direction.setOnClickListener(new DirectionListener(new BigDecimal(sale_delta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				vh.sale_delta_direction.setImageDrawable(null);
			}
			
			// ///////////////////////////////////////////////////////////////////////////////////////////////
			
			registerForContextMenu(item);
			
			list.addView(item);
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getCityCode() {
		return cityCode;
	}
	
	public void setLoadStatusComplete() {
		isLoadRunning = false;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class ViewHolder {
		
		public ImageView currency_icon;
		
		public TextView lbl;
		
		public TextView buy_rate;
		public TextView sale_rate;
		
		public ImageView buy_delta_direction;
		public ImageView sale_delta_direction;
	}
	
	class CitySpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			String newCityCode = getResources().getStringArray(R.array.city_spinner_key)[((Spinner) mainLayout.findViewById(R.id.city_code_spinner)).getSelectedItemPosition()];
			boolean isEqualsCity = cityCode.equalsIgnoreCase(newCityCode);
						
			cityCode = getResources().getStringArray(R.array.city_spinner_key)[((Spinner) mainLayout.findViewById(R.id.city_code_spinner)).getSelectedItemPosition()];
			addValue2Pref("city_code", String.valueOf(((Spinner) mainLayout.findViewById(R.id.city_code_spinner)).getSelectedItemPosition()));
			
			if (!isLoadRunning && !isEqualsCity) {
				isLoadRunning = true;
				((MainActivity)getActivity()).loadRates();
			}			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
						
		}		
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		createCurrencySelectionDialog();	    
	}
	
	public void createCurrencySelectionDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.black_market_currencys_dialog).length];
		
		Set<String> selectedItems = getSetFromPref("black_market_currency_selection");
		
		if (selectedItems == null) {
			
			for (int i = 0; i < getResources().getStringArray(R.array.black_market_currencys_dialog).length; i++)
		        checkedItems[i] = true;
		}
		else {
			
			for (int i = 0; i < getResources().getStringArray(R.array.black_market_currencys_dialog).length; i++)
		        checkedItems[i] = selectedItems.contains(getResources().getStringArray(R.array.black_market_currencys_dialog)[i]);
		}	    
	    
	    builder.setTitle(R.string.dialog_title);
	    builder.setMultiChoiceItems(R.array.black_market_currencys_dialog, null,
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
								mSelectedItems.add(getResources().getStringArray(R.array.black_market_currencys_dialog)[i]);
							}
						}
						
						addSet2Pref("black_market_currency_selection", new HashSet<String>(mSelectedItems));
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
}