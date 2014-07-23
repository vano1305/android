package dn.ivan.actionbarexample.fragments;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import dn.ivan.actionbarexample.R;

public class HistoryFragment extends Fragment {
	
	double[] buyRates  = {8.17, 8.19, 8.18, 8.21, 8.20};
	double[] sellRates = {8.18, 8.19, 8.20, 8.21, 8.18};
	
	View rootView;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.history_tab_layout, container, false);
		
		GraphicalView lineChartView = ChartFactory.getLineChartView(getActivity(), getDemoDataset(), getDemoRenderer());
		
		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
		layout.addView(lineChartView);
		
		return rootView;
	}
	
	private XYMultipleSeriesDataset getDemoDataset() {
		
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    XYSeries series = null;
	    
	    // //////////////////////////////////////////
	    	    	    
	    series = new XYSeries("ПРОДАЖА");
        for (int k = 0; k < sellRates.length; k++) {
            series.add(k+1, sellRates[k]);
        }
        dataset.addSeries(series);
        
        // //////////////////////////////////////////
        
        series = new XYSeries("ПОКУПКА");
        for (int k = 0; k < buyRates.length; k++) {
            series.add(k+1, buyRates[k]);
        }
        dataset.addSeries(series);
        
        // //////////////////////////////////////////
	    
	    return dataset;
	}
	
	private XYMultipleSeriesRenderer getDemoRenderer() {
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = null;
		
		renderer.setAxisTitleTextSize(25);//Размер подписей осей
	    renderer.setChartTitleTextSize(25);//Зазмер заголовка
	    renderer.setLabelsTextSize(25);
	    renderer.setLegendTextSize(20);//Размер текста легенды
	    renderer.setPointSize(5f);
	    renderer.setMargins(new int[] { 50, 50, 50, 50 });//Отступы
	    
	    // /////////////////////////////////////////////////////////
	    
	    r = new XYSeriesRenderer();
	    r.setColor(Color.RED);
	    r.setPointStyle(PointStyle.CIRCLE);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    
	    // //////////////////////////////////////////////////////////
	    
	    r = new XYSeriesRenderer();
	    r.setColor(Color.BLUE);
	    r.setPointStyle(PointStyle.CIRCLE);
	    r.setFillPoints(true);
	    renderer.addSeriesRenderer(r);
	    
	    // //////////////////////////////////////////////////////////
	    
	    setChartSettings(renderer);
	    
	    return renderer;
	}
	
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {

		renderer.setChartTitle("Динамика изменения курса");
		renderer.setXTitle("ДАТА");
		renderer.setYTitle("КУРС");
		renderer.setApplyBackgroundColor(false);
		renderer.setFitLegend(false);
		renderer.setAxesColor(Color.BLACK);
		renderer.setShowGrid(true);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(6);
		renderer.setYAxisMin(8);
		renderer.setYAxisMax(8.5);
		renderer.setZoomEnabled(false);

		// ////////////////////////////////////////////////////

		renderer.setMarginsColor(Color.WHITE);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
	}
}