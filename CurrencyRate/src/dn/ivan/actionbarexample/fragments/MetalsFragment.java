package dn.ivan.actionbarexample.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.R;
import dn.ivan.actionbarexample.fragments.logic.Rates;

public class MetalsFragment extends Fragment {
	
	View rootView;
	
	ArrayList<Object> rates;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
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
	
	public void setData(ArrayList<Object> rates) {
		
		this.rates = rates;
		
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
		
		for (int i = 0; i < rates.size(); i++) {
			
			Rates ratesItem = (Rates) rates.get(i);
		    
		    View item = ltInflater.inflate(R.layout.metals_item_layout, null, false);
		    
		    TextView lstItemMetalsLbl = (TextView) item.findViewById(R.id.lstItemMetalsLbl);
		    lstItemMetalsLbl.setText(Html.fromHtml("<b>" + ratesItem.char3 + "</b>" + " (" + getResources().getString(getResources().getIdentifier(ratesItem.char3, "string", getActivity().getPackageName())) + ")"));
		    
		    TextView lstItemMetalsRate = (TextView) item.findViewById(R.id.lstItemMetalsRate);
		    lstItemMetalsRate.setText(Html.fromHtml("<b>" + ratesItem.rate + "</b>" + " " + getString(R.string.for_items) + " " + "<b>" + ratesItem.size + "</b>" + " " + getString(R.string.ounce)));
		    
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
		    
		    metals_date.setText("ÊÓÐÑÛ ÍÀ " + ratesItem.date);
		    
		    registerForContextMenu(item);
		    
		    list.addView(item);
		}
	}
}