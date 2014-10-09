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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.CommercialRates;

public class CommercialFragment extends Fragment implements OnItemSelectedListener {
	
	Toast currentToast = null;
	
	ArrayList<CommercialRates> notSortRates;
	
	View mainLayout = null;
    View averageRatesItem = null;
	
	String currencyCode;
	
	public static CommercialFragment newInstance(String currencyCode) {
		
		CommercialFragment currency = new CommercialFragment();
				
		Bundle bundle = new Bundle();
		bundle.putString("argumentsCurrencyCode", currencyCode);
		currency.setArguments(bundle);
		
	    return currency;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public View onCreateView(LayoutInflater ltInflater, ViewGroup container, Bundle savedInstanceState) {
		
		mainLayout = ltInflater.inflate(R.layout.commercial_tab_layout, container, false);
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.commercial_list);
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
		
		Spinner spinner = (Spinner) mainLayout.findViewById(R.id.currencys_spinner);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Currency_value, R.layout.currency_spinner_pattern);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		if (notSortRates != null) {
			setData(notSortRates);
		}
		
		return mainLayout;
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void createAverageRatesItem() {
		
		averageRatesItem = getActivity().getLayoutInflater().inflate(R.layout.commercial_average_item_layout, null, false);
		
		TextView averageItemLbl = (TextView) averageRatesItem.findViewById(R.id.average_txt);
	    averageItemLbl.setText(getString(R.string.commercial_average));
	    
	    TextView averageBuyTxt = (TextView) averageRatesItem.findViewById(R.id.average_buy_txt);
	    averageBuyTxt.setText(Html.fromHtml("<b>" + getString(R.string.buy) + "</b>"));
	    
	    TextView averageSellTxt = (TextView) averageRatesItem.findViewById(R.id.average_sell_txt);
	    averageSellTxt.setText(Html.fromHtml("<b>" + getString(R.string.sell) + "</b>"));
	    
	    TextView averageBuy = (TextView) averageRatesItem.findViewById(R.id.average_buy_result);
	    averageBuy.setText("0.0");
	    			    
	    TextView averageSell = (TextView) averageRatesItem.findViewById(R.id.average_sell_result);
	    averageSell.setText("0.0");
	    
	    ((LinearLayout) mainLayout.findViewById(R.id.average_rates)).addView(averageRatesItem);
	}
	
	@SuppressWarnings("unchecked")
	public void setData(Object rates_) {
		
		notSortRates = (ArrayList<CommercialRates>) rates_;
						
		// //////////////////////////////////////////////////////////////////////////////////
		
		ArrayList<CommercialRates> rates = new ArrayList<CommercialRates>();
		for (int i = 0; i < notSortRates.size(); i++) {
			
			if (currencyCode.equalsIgnoreCase(notSortRates.get(i).codeAlpha)) {
				rates.add(notSortRates.get(i));
			}
		}
		
		// ///////////////////////////////////////////////////////////////////////////
		
		ArrayList<CommercialRates> checkedBanks = null;
		
		Set<String> banksSet = null;		
		banksSet = getSetFromPref("selected_banks");
		
		if (banksSet != null) {
			
			checkedBanks = new ArrayList<CommercialRates>();
			
			for (int i = 0; i < rates.size(); i++) {
				
				CommercialRates item = (CommercialRates) rates.get(i);
				if (banksSet.contains(item.sourceUrl.replaceAll("http://bank-ua.com/banks/", "").replaceAll("/", "").trim())) {
					checkedBanks.add(item);
				}
			}
		}
		else {			
			checkedBanks = new ArrayList<CommercialRates>(rates);
		}
		
		rates = checkedBanks;
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		ScrollView scrollView = ((ScrollView) mainLayout.findViewById(R.id.commercial_scroll));
		scrollView.setScrollbarFadingEnabled(true);
		
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.commercial_list);
		list.removeAllViews();
		
		LayoutInflater lInflater = getActivity().getLayoutInflater();
		
		View stub = lInflater.inflate(R.layout.commercial_triangle_stub, null, false);
		list.addView(stub);
		
		TextView current_date = (TextView) averageRatesItem.findViewById(R.id.average_txt);
		current_date.setText(" ”–—€ Õ¿ " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
		
		double totalBuy = 0.0;
		double totalSell = 0.0;
		
		double totalBuyDelta = 0.0;
		double totalSellDelta = 0.0;
		
		for (int i = 0; i < rates.size(); i++) {
			
			CommercialRates ratesItem = (CommercialRates) rates.get(i);
			
			View item = lInflater.inflate(R.layout.commercial_item_layout, null, false);
			
			// ////////////////////////////////////////////////////////////////////
			
			ViewHolder vh = new ViewHolder();
			
			vh.lstItemCurrency = (TextView) item.findViewById(R.id.bank_name_txt);
			vh.lstItemBuyResult = (TextView) item.findViewById(R.id.commercial_buy_txt);
			vh.buy_direction = (ImageView) item.findViewById(R.id.commercial_buy_direction_img);
			vh.lstItemSellResult = (TextView) item.findViewById(R.id.commercial_sell_txt);
			vh.sell_direction = (ImageView) item.findViewById(R.id.commercial_sell_direction_img);
			vh.bank_icon = (ImageView) item.findViewById(R.id.bank_icon);
			
			if (getResources().getIdentifier(ratesItem.sourceUrl.replaceAll("http://bank-ua.com/banks/", "").replaceAll("/", "").trim(), "string", getActivity().getPackageName()) != 0) {
				vh.lstItemCurrency.setText(Html.fromHtml("<b>" + getResources().getString(getResources().getIdentifier(ratesItem.sourceUrl.replaceAll("http://bank-ua.com/banks/", "").replaceAll("/", "").trim(), "string", getActivity().getPackageName())) + "</b>"));
			}
			else {
				vh.lstItemCurrency.setText(Html.fromHtml("<b>" + ratesItem.bankName + "</b>"));
			}
			
			if ("RUB".equalsIgnoreCase(currencyCode)) {
				
				vh.lstItemBuyResult.setText(Html.fromHtml("<b>" + new BigDecimal(ratesItem.rateBuy).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
				vh.lstItemSellResult.setText(Html.fromHtml("<b>" + new BigDecimal(ratesItem.rateSale).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			}
			else {
				
				vh.lstItemBuyResult.setText(Html.fromHtml("<b>" + new BigDecimal(ratesItem.rateBuy).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
				vh.lstItemSellResult.setText(Html.fromHtml("<b>" + new BigDecimal(ratesItem.rateSale).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
			}
			
			if (Double.valueOf(ratesItem.rateSaleDelta) > 0) {
				
				vh.sell_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.sell_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				if ("RUB".equalsIgnoreCase(currencyCode)) {
					vh.sell_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(ratesItem.rateSaleDelta).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
				else {
					vh.sell_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(ratesItem.rateSaleDelta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
			}
			else if (Double.valueOf(ratesItem.rateSaleDelta) < 0) {
				
				vh.sell_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.sell_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				if ("RUB".equalsIgnoreCase(currencyCode)) {
					vh.sell_direction.setOnClickListener(new DirectionListener(new BigDecimal(ratesItem.rateSaleDelta).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
				else {
					vh.sell_direction.setOnClickListener(new DirectionListener(new BigDecimal(ratesItem.rateSaleDelta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
			}
			
			if (Double.valueOf(ratesItem.rateBuyDelta) > 0) {
				
				vh.buy_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.buy_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				if ("RUB".equalsIgnoreCase(currencyCode)) {
					vh.buy_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(ratesItem.rateBuyDelta).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
				else {
					vh.buy_direction.setOnClickListener(new DirectionListener("+" + new BigDecimal(ratesItem.rateBuyDelta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
			}
			else if (Double.valueOf(ratesItem.rateBuyDelta) < 0) {
				
				vh.buy_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.buy_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
				if ("RUB".equalsIgnoreCase(currencyCode)) {
					vh.buy_direction.setOnClickListener(new DirectionListener(new BigDecimal(ratesItem.rateBuyDelta).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
				else {
					vh.buy_direction.setOnClickListener(new DirectionListener(new BigDecimal(ratesItem.rateBuyDelta).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
				}
			}
			
			try {
				vh.bank_icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(ratesItem.sourceUrl.replaceAll("http://bank-ua.com/banks/", "").replaceAll("/", "").trim(), "drawable", getActivity().getPackageName())));
				vh.bank_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			catch (Exception e) {
				
			}
			
			registerForContextMenu(item);
			
			list.addView(item);
			
			// //////////////////////////////////////////////////////////////////////////////////////
			
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalBuyDelta = new BigDecimal(totalBuyDelta).add(new BigDecimal(ratesItem.rateBuyDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSellDelta = new BigDecimal(totalSellDelta).add(new BigDecimal(ratesItem.rateSaleDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		// /////////////////////////////////////////////////////////////////////////////////
		
		//TextView lblAverage = (TextView) averageRatesItem.findViewById(R.id.average_txt);
		//lblAverage.setText(R.string.commercial_average);
		
		TextView averageBuy = (TextView) averageRatesItem.findViewById(R.id.average_buy_result);
		if ("RUB".equalsIgnoreCase(currencyCode)) {			
			averageBuy.setText(Html.fromHtml("<b>" + new BigDecimal(rates.size() == 0? 0: (totalBuy / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
		}
		else {			
			averageBuy.setText(Html.fromHtml("<b>" + new BigDecimal(rates.size() == 0? 0: (totalBuy / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
		}	    
	    			    
	    TextView averageSell = (TextView) averageRatesItem.findViewById(R.id.average_sell_result);
	    if ("RUB".equalsIgnoreCase(currencyCode)) {			
	    	averageSell.setText(Html.fromHtml("<b>" + new BigDecimal(rates.size() == 0? 0: (totalSell / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
		}
		else {			
			averageSell.setText(Html.fromHtml("<b>" + new BigDecimal(rates.size() == 0? 0: (totalSell / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
		}
	    
	    // //////////////////////////////////////////////////////////////////////////////////
	    
	    ImageView sellDirection = (ImageView) averageRatesItem.findViewById(R.id.commercial_average_sell_direction_img);
	    if (Double.valueOf(totalSellDelta / rates.size()) > 0) {
	    	
	    	sellDirection.setImageDrawable(getResources().getDrawable(R.drawable.up));
			sellDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if ("RUB".equalsIgnoreCase(currencyCode)) {
				sellDirection.setOnClickListener(new DirectionListener("+" + new BigDecimal(Double.valueOf(totalSellDelta / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				sellDirection.setOnClickListener(new DirectionListener("+" + new BigDecimal(Double.valueOf(totalSellDelta / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
		}
		else if (Double.valueOf(totalSellDelta / rates.size()) < 0) {
			
			sellDirection.setImageDrawable(getResources().getDrawable(R.drawable.down));
			sellDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if ("RUB".equalsIgnoreCase(currencyCode)) {
				sellDirection.setOnClickListener(new DirectionListener(new BigDecimal(Double.valueOf(totalSellDelta / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				sellDirection.setOnClickListener(new DirectionListener(new BigDecimal(Double.valueOf(totalSellDelta / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
		}
		else {
			sellDirection.setImageDrawable(null);
		}
		
		ImageView buyDirection = (ImageView) averageRatesItem.findViewById(R.id.commercial_average_buy_direction_img);
		if (Double.valueOf(totalBuyDelta / rates.size()) > 0) {
			
			buyDirection.setImageDrawable(getResources().getDrawable(R.drawable.up));
			buyDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if ("RUB".equalsIgnoreCase(currencyCode)) {
				buyDirection.setOnClickListener(new DirectionListener("+" + new BigDecimal(Double.valueOf(totalBuyDelta / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				buyDirection.setOnClickListener(new DirectionListener("+" + new BigDecimal(Double.valueOf(totalBuyDelta / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
		}
		else if (Double.valueOf(totalBuyDelta / rates.size()) < 0) {
			
			buyDirection.setImageDrawable(getResources().getDrawable(R.drawable.down));
			buyDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if ("RUB".equalsIgnoreCase(currencyCode)) {
				buyDirection.setOnClickListener(new DirectionListener(new BigDecimal(Double.valueOf(totalBuyDelta / rates.size())).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
			else {
				buyDirection.setOnClickListener(new DirectionListener(new BigDecimal(Double.valueOf(totalBuyDelta / rates.size())).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()));
			}
		}
		else {
			buyDirection.setImageDrawable(null);
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		createBanksSelectionDialog();    
	}
	
	public void createBanksSelectionDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.banks_dialog_key).length];
		
		Set<String> selectedItems = getSetFromPref("selected_banks");
		
		if (selectedItems == null) {
			
			for (int i = 0; i < getResources().getStringArray(R.array.banks_dialog_key).length; i++)
		        checkedItems[i] = true;
		}
		else {
			
			for (int i = 0; i < getResources().getStringArray(R.array.banks_dialog_key).length; i++)
		        checkedItems[i] = selectedItems.contains(getResources().getStringArray(R.array.banks_dialog_key)[i]);
		}	    
	    
	    builder.setTitle("¬˚·Ó ·‡ÌÍÓ‚");
	    builder.setMultiChoiceItems(R.array.banks_dialog_value, null,
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
								mSelectedItems.add(getResources().getStringArray(R.array.banks_dialog_key)[i]);
							}
						}
						
						addSet2Pref("selected_banks", new HashSet<String>(mSelectedItems));
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
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		currencyCode = getResources().getStringArray(R.array.Currency_key)[((Spinner) mainLayout.findViewById(R.id.currencys_spinner)).getSelectedItemPosition()];
		
		if (notSortRates == null) {
			((MainActivity)getActivity()).loadRates();
		}
		else {
			setData(notSortRates);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
				
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class ViewHolder {
		
		public TextView lstItemCurrency;
						
		public TextView lstItemBuyResult;
		public ImageView buy_direction;
			
		public TextView lstItemSellResult;
		public ImageView sell_direction;
		
		public ImageView bank_icon;
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
}