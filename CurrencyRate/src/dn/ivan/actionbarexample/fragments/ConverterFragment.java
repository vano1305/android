package dn.ivan.actionbarexample.fragments;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.DataHolder;
import dn.ivan.actionbarexample.logic.NbuRates;

public class ConverterFragment extends Fragment implements OnItemSelectedListener {
	
	static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());		
	static {
		
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(' ');
	}
	
	static DecimalFormat df = new DecimalFormat("###,###,###,##0.00", dfs);
	static {
		df.setGroupingSize(3);
	}
	
	private String rate1 = "";
	private String rate2 = "";
	
	private String currency1 = "";
	private String currency2 = "";
	
	private View rootView;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.converter_layout, container, false);
		
		// /////////////////////////////////////////////////////////////////////////
		
		Spinner spinner1 = (Spinner) rootView.findViewById(R.id.converter_currency_1);
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.nbu_currencys, R.layout.currency_spinner_pattern);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner1.setOnItemSelectedListener(this);
				
		Spinner spinner2 = (Spinner) rootView.findViewById(R.id.converter_currency_2);
		
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.nbu_currencys, R.layout.currency_spinner_pattern);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(this);
				
		((Button)rootView.findViewById(R.id.converter_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				convert();
			}
		});
				
		((Button)rootView.findViewById(R.id.clear_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((EditText)rootView.findViewById(R.id.converter_amount)).setText("0");
				convert();				
			}
		});
		
		((EditText)rootView.findViewById(R.id.converter_amount)).setOnEditorActionListener(new OnEditorActionListener() {
			
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        	
	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
	                convert();	            	
	            }    
	            return false;
	        }
	    });
		
		return rootView;
	}
	
	protected void convert() {
		
		try {
			
			if (((Spinner) rootView.findViewById(R.id.converter_currency_1)).getSelectedItemPosition() == -1 || ((Spinner) rootView.findViewById(R.id.converter_currency_2)).getSelectedItemPosition() == -1) {
				return;
			}
			
			if (DataHolder.nbuRatesItem == null) {
				
				Toast.makeText(getActivity(), "Загрузите курсы НБУ!", Toast.LENGTH_LONG).show();
				return;
			}
			
			currency1 = getResources().getStringArray(R.array.nbu_currencys)[((Spinner) rootView.findViewById(R.id.converter_currency_1)).getSelectedItemPosition()];
			currency1 = currency1.substring(0, 3);
			
			currency2 = getResources().getStringArray(R.array.nbu_currencys)[((Spinner) rootView.findViewById(R.id.converter_currency_2)).getSelectedItemPosition()];
			currency2 = currency2.substring(0, 3);
			
			for (int i = 0; i < DataHolder.nbuRatesItem.size(); i++) {
				
				NbuRates item = (NbuRates) DataHolder.nbuRatesItem.get(i);
				if (item.char3.equalsIgnoreCase(currency1)) {
					
					MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
					rate1 = new BigDecimal(item.rate).divide(new BigDecimal(item.size, mc)).toPlainString();
				}
			}
			
			for (int i = 0; i < DataHolder.nbuRatesItem.size(); i++) {
				
				NbuRates item = (NbuRates) DataHolder.nbuRatesItem.get(i);
				if (item.char3.equalsIgnoreCase(currency2)) {
					
					MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
					rate2 = new BigDecimal(item.rate).divide(new BigDecimal(item.size, mc)).toPlainString();
				}
			}
			
			MathContext mc = new MathContext(8, RoundingMode.HALF_UP);
			double crossRate = new BigDecimal(rate1).divide(new BigDecimal(rate2), mc).doubleValue();
			
			((EditText)rootView.findViewById(R.id.converter_result)).setText(df.format(new BigDecimal(String.valueOf(((EditText)rootView.findViewById(R.id.converter_amount)).getText())).multiply(new BigDecimal(String.valueOf(crossRate))).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));
		}
		catch (Exception e) {
			
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		convert();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
				
	}
}