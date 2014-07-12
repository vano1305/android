package dn.ivan.actionbarexample.fragments;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.fragments.logic.CommercialRates;

public class CommercialFragment extends Fragment implements OnItemSelectedListener {
	
	ArrayList<CommercialRates> rates;
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
		
		rates = new ArrayList<CommercialRates>();
		for (int i = 0; i < notSortRates.size(); i++) {
			
			if (currencyCode.equalsIgnoreCase(notSortRates.get(i).codeAlpha)) {
				rates.add(notSortRates.get(i));
			}
		}
		
		// //////////////////////////////////////////////////////////////////////////////////
		
		ScrollView scrollView = ((ScrollView) mainLayout.findViewById(R.id.commercial_scroll));
		scrollView.setScrollbarFadingEnabled(true);
		
		LinearLayout list = (LinearLayout) mainLayout.findViewById(R.id.commercial_list);
		list.removeAllViews();
		
		LayoutInflater lInflater = getActivity().getLayoutInflater();
		
		View stub = lInflater.inflate(R.layout.commercial_triangle_stub, null, false);
		list.addView(stub);
		
		View direction = lInflater.inflate(R.layout.commercial_direction_layout, null, false);
		list.addView(direction);
		
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
			
			vh.lstItemCurrency.setText(Html.fromHtml("<b>" + getResources().getString(getResources().getIdentifier(ratesItem.sourceUrl.replaceAll("http://bank-ua.com/banks/", "").replaceAll("/", "").trim(), "string", getActivity().getPackageName())) + "</b>"));

			vh.lstItemBuyResult.setText(Html.fromHtml("<b>" + ratesItem.rateBuy + "</b>"));
			vh.lstItemSellResult.setText(Html.fromHtml("<b>" + ratesItem.rateSale + "</b>"));
			
			if (Double.valueOf(ratesItem.rateSaleDelta) > 0) {
				
				vh.sell_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.sell_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			else if (Double.valueOf(ratesItem.rateSaleDelta) < 0) {
				
				vh.sell_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.sell_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			
			if (Double.valueOf(ratesItem.rateBuyDelta) > 0) {
				
				vh.buy_direction.setImageDrawable(getResources().getDrawable(R.drawable.up));
				vh.buy_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			else if (Double.valueOf(ratesItem.rateBuyDelta) < 0) {
				
				vh.buy_direction.setImageDrawable(getResources().getDrawable(R.drawable.down));
				vh.buy_direction.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			
			registerForContextMenu(item) ;			
			
			list.addView(item);
			
			// //////////////////////////////////////////////////////////////////////////////////////
			
			totalBuy = new BigDecimal(totalBuy).add(new BigDecimal(ratesItem.rateBuy)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalBuyDelta = new BigDecimal(totalBuyDelta).add(new BigDecimal(ratesItem.rateBuyDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    
		    totalSell = new BigDecimal(totalSell).add(new BigDecimal(ratesItem.rateSale)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    totalSellDelta = new BigDecimal(totalSellDelta).add(new BigDecimal(ratesItem.rateSaleDelta)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		// /////////////////////////////////////////////////////////////////////////////////
		
		TextView lblAverage = (TextView) averageRatesItem.findViewById(R.id.average_txt);
		lblAverage.setText(R.string.commercial_average);
		
		TextView averageBuy = (TextView) averageRatesItem.findViewById(R.id.average_buy_result);
	    averageBuy.setText(Html.fromHtml("<b>" + new BigDecimal(totalBuy / rates.size()).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
	    			    
	    TextView averageSell = (TextView) averageRatesItem.findViewById(R.id.average_sell_result);
	    averageSell.setText(Html.fromHtml("<b>" + new BigDecimal(totalSell / rates.size()).setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString() + "</b>"));
	    
	    // //////////////////////////////////////////////////////////////////////////////////
	    
	    ImageView sellDirection = (ImageView) averageRatesItem.findViewById(R.id.commercial_average_sell_direction_img);
	    if (Double.valueOf(totalSellDelta / rates.size()) > 0) {
	    	
	    	sellDirection.setImageDrawable(getResources().getDrawable(R.drawable.up));
			sellDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		else if (Double.valueOf(totalSellDelta / rates.size()) < 0) {
			
			sellDirection.setImageDrawable(getResources().getDrawable(R.drawable.down));
			sellDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		
		ImageView buyDirection = (ImageView) averageRatesItem.findViewById(R.id.commercial_average_buy_direction_img);
		if (Double.valueOf(totalBuyDelta / rates.size()) > 0) {
			
			buyDirection.setImageDrawable(getResources().getDrawable(R.drawable.up));
			buyDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		else if (Double.valueOf(totalBuyDelta / rates.size()) < 0) {
			
			buyDirection.setImageDrawable(getResources().getDrawable(R.drawable.down));
			buyDirection.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		currencyCode = getResources().getStringArray(R.array.Currency_key)[((Spinner) mainLayout.findViewById(R.id.currencys_spinner)).getSelectedItemPosition()];
		
		if (notSortRates == null) {
			((MainActivity)getActivity()).loadRates(null);
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
	}
}