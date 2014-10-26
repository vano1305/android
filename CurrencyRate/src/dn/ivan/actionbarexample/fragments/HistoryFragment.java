package dn.ivan.actionbarexample.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.logic.DataManager;
import dn.ivan.actionbarexample.logic.DataManager.NbuRatesHolderForChart;

public class HistoryFragment extends BaseFragment implements OnItemSelectedListener {
	
	private ArrayList<DataManager.NbuRatesHolderForChart> ratesHolder;
	
	private String currency = "";
	
	private double min = 0;
	private double max = 0;
	
	public static final int TEXT_SIZE_XXHDPI = 31;
	public static final int TEXT_SIZE_XHDPI = 24;
	public static final int TEXT_SIZE_HDPI = 20;
	public static final int TEXT_SIZE_MDPI = 18;
	public static final int TEXT_SIZE_LDPI = 13;
	
	private View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.history_tab_layout, container, false);
		
		// /////////////////////////////////////////////////////////////////////////
		
		Spinner spinner = (Spinner) rootView.findViewById(R.id.nbu_currency_history_spinner);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.nbu_currencys, R.layout.currency_spinner_pattern);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(Integer.valueOf(getValueFromPref("curr_hist", "0")));
		spinner.setOnItemSelectedListener(this);
		
		((ImageView)rootView.findViewById(R.id.imageView1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog((EditText)rootView.findViewById(R.id.date1));
			}
		});
		
		((ImageView)rootView.findViewById(R.id.imageView2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog((EditText)rootView.findViewById(R.id.date2));
			}
		});
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		
		Calendar ca1 = Calendar.getInstance();
        ca1.setTime(new Date());
        
        long milisecond = ca1.getTimeInMillis();
        
        milisecond = milisecond - (7 * 24 * 60 * 60 * 1000);
        
        Calendar ca2 = Calendar.getInstance();
        ca2.setTimeInMillis(milisecond);
		
		((EditText)rootView.findViewById(R.id.date2)).setText(dateFormat.format(new Date()));
		((EditText)rootView.findViewById(R.id.date1)).setText(dateFormat.format(ca2.getTime()));
		
		// /////////////////////////////////////////////////////////////////////////
		
		((Button)rootView.findViewById(R.id.reload_chart)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createChart();
			}
		});
		
		// /////////////////////////////////////////////////////////////////////////
		
		if (ratesHolder == null) {
			createChart();
		}
		else {
			reinitChart();			
		}		
		
		return rootView;
	}
	
	protected void createChart() {
		
		String selectedCurrency = getResources().getStringArray(R.array.nbu_currencys)[((Spinner) rootView.findViewById(R.id.nbu_currency_history_spinner)).getSelectedItemPosition()];
		currency = selectedCurrency.substring(0, 3);
		
		String date1 = ((EditText)rootView.findViewById(R.id.date1)).getText().toString();
		String date2 = ((EditText)rootView.findViewById(R.id.date2)).getText().toString();
		
		if ("".equalsIgnoreCase(date1) || "".equalsIgnoreCase(date2)) {
			Toast.makeText(getActivity(), "Введите диапазон дат!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ratesHolder = new DataManager().selectRatesNbu(getActivity(), currency, date1, date2);
		
		GraphicalView lineChartView = null;
		try {
			lineChartView = ChartFactory.getTimeChartView(getActivity(), getDemoDataset(ratesHolder), getDemoRenderer(ratesHolder), "dd/MM/yyyy");
			((TextView)rootView.findViewById(R.id.chart_title)).setText("Динамика изменения курса " + currency);
		}
		catch (Exception e) {
			Log.v("CHART", e.toString());
		}		
		
		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
		layout.removeAllViews();
		layout.addView(lineChartView);
	}
	
	protected void reinitChart() {
		
		GraphicalView lineChartView = null;
		try {
			lineChartView = ChartFactory.getTimeChartView(getActivity(), getDemoDataset(ratesHolder), getDemoRenderer(ratesHolder), "dd/MM/yyyy");
			((TextView)rootView.findViewById(R.id.chart_title)).setText("Динамика изменения курса " + currency);
		}
		catch (Exception e) {
			Log.v("CHART", e.toString());
		}		
		
		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
		layout.addView(lineChartView);
	}
	
	private XYMultipleSeriesDataset getDemoDataset(ArrayList<NbuRatesHolderForChart> rates) throws Exception {
		
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    TimeSeries series = null;
	    
	    // //////////////////////////////////////////
	    	    	    
	    series = new TimeSeries("");
        for (int k = 0; rates != null && k < rates.size(); k++) {
        	series.add(rates.get(k).date, rates.get(k).rate);
        }
        dataset.addSeries(series);
        
        min = series.getMinY();
        max = series.getMaxY();
        
        // //////////////////////////////////////////
	    
	    return dataset;
	}
	
	private XYMultipleSeriesRenderer getDemoRenderer(ArrayList<NbuRatesHolderForChart> rates) {
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = null;
		
		// /////////////////////////////////////////////////////////
	    
	    r = new XYSeriesRenderer();
	    r.setColor(Color.WHITE);
	    //r.setPointStyle(PointStyle.CIRCLE);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    
	    if (getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XXHIGH || getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XHIGH) {
	    	r.setLineWidth(4);
	    }
	    else {
	    	r.setLineWidth(2);
	    }
	    // //////////////////////////////////////////////////////////
	    
	    setChartSettings(renderer);
	    
	    if (rates != null && rates.size() != 0) {
	    	renderer.setYAxisMin(min * 0.95);
	    	renderer.setYAxisMax(max * 1.05);
	    }
	    
	    return renderer;
	}
	
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
		
		renderer.setXAxisColor(Color.WHITE);
		renderer.setYAxisColor(Color.WHITE);		
		renderer.setLabelsColor(Color.WHITE);
		renderer.setAxesColor(Color.WHITE);
		renderer.setXLabelsColor(Color.WHITE);
		renderer.setYLabelsColor(0, Color.WHITE);
		
		renderer.setShowLegend(false);
		
		// /////////////////////////////////////////////////////////////////////////////////
		
		switch (getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_XXHIGH:
			renderer.setMargins(new int[] { 10, 130, 50, 10 });
			renderer.setAxisTitleTextSize(TEXT_SIZE_XXHDPI);
			renderer.setChartTitleTextSize(TEXT_SIZE_XXHDPI);
			renderer.setLabelsTextSize(TEXT_SIZE_XXHDPI);
			renderer.setLegendTextSize(TEXT_SIZE_XXHDPI);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			renderer.setMargins(new int[] { 10,100, 50, 10 });
			renderer.setAxisTitleTextSize(TEXT_SIZE_XHDPI);
			renderer.setChartTitleTextSize(TEXT_SIZE_XHDPI);
			renderer.setLabelsTextSize(TEXT_SIZE_XHDPI);
			renderer.setLegendTextSize(TEXT_SIZE_XHDPI);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			renderer.setMargins(new int[] { 10, 60, 30, 10 });
			renderer.setAxisTitleTextSize(TEXT_SIZE_HDPI);
			renderer.setChartTitleTextSize(TEXT_SIZE_HDPI);
			renderer.setLabelsTextSize(TEXT_SIZE_HDPI);
			renderer.setLegendTextSize(TEXT_SIZE_HDPI);
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			renderer.setMargins(new int[] { 10, 60, 30, 10 });
			renderer.setAxisTitleTextSize(TEXT_SIZE_MDPI);
			renderer.setChartTitleTextSize(TEXT_SIZE_MDPI);
			renderer.setLabelsTextSize(TEXT_SIZE_MDPI);
			renderer.setLegendTextSize(TEXT_SIZE_MDPI);
			break;
		default:
			renderer.setMargins(new int[] { 10, 60, 30, 10 });
			renderer.setAxisTitleTextSize(TEXT_SIZE_LDPI);
			renderer.setChartTitleTextSize(TEXT_SIZE_LDPI);
			renderer.setLabelsTextSize(TEXT_SIZE_LDPI);
			renderer.setLegendTextSize(TEXT_SIZE_LDPI);
			break;
		}
		
		renderer.setXLabelsAngle(25);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.RIGHT);
		
		renderer.setMarginsColor(Color.rgb(80, 118, 153));
		
		renderer.setPanEnabled(true, true);
		renderer.setShowGrid(true);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		createChart();
		
		addValue2Pref("curr_hist", String.valueOf(((Spinner) rootView.findViewById(R.id.nbu_currency_history_spinner)).getSelectedItemPosition()));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
				
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void showDatePickerDialog(EditText txt) {
		
	    DialogFragment newFragment = new DatePickerFragment(txt);
	    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
	}
	
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		EditText txt;
		
		public DatePickerFragment(EditText txt_) {
			txt = txt_;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			final Calendar c;
			
			int year;
			int month;
			int day;
			
			if ("".equalsIgnoreCase(txt.getText().toString())) {
				
				c = Calendar.getInstance();
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}
			else {
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
				c = Calendar.getInstance();
				try {
					c.setTime(dateFormat.parse(txt.getText().toString()));
				}
				catch (ParseException e) {
					Log.v("CHART", e.toString());
				}
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);				
			}			

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
			txt.setText(dateFormat.format(new GregorianCalendar(year, month, day).getTime()));
			createChart();
		}
	}
}