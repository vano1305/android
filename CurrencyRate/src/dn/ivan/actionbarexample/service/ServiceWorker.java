package dn.ivan.actionbarexample.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import dn.ivan.actionbarexample.MainActivity;
import dn.ivan.actionbarexample.logic.CommercialRates;
import dn.ivan.actionbarexample.logic.MetalsRates;
import dn.ivan.actionbarexample.logic.NbuRates;
import dn.ivan.actionbarexample.logic.Rates;

public class ServiceWorker implements Runnable {

	ArrayList<Object> ratesList = new ArrayList<Object>();
	
	// //////////////////////////////////////////////////

	private Context context;
	private String source = "";
	private String from = "";
	
	public ServiceWorker(Context context, String source, String from) {

		this.context = context;
		this.source = source;
		this.from = from;
	}

	@Override
	public void run() {

		BufferedReader in = null;

		try {
			
			if (from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET)) {
				context.sendBroadcast(new Intent(MainActivity.START_LOAD).putExtra(MainActivity.SOURCE, source).putExtra(MainActivity.FROM, from));
			}			

			// ////////////////////////////////////////////////////////////////////////////////////

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = null;

			if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
				request = new HttpGet("http://bank-ua.com/export/exchange_rate_cash.xml");
			}
			else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {				
				request = new HttpGet("http://bank-ua.com/export/currrate.xml");				
			}
			else {
				request = new HttpGet("http://bank-ua.com/export/metalrate.xml");
			}

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
				in = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			}
			else {
				in = new BufferedReader(new InputStreamReader(entity.getContent(), "cp1251"));
			}
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(in);

			// /////////////////////////////////////////////////////////////////////////////////////

			Object item = null;
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

				switch (xpp.getEventType()) {

				case XmlPullParser.START_TAG:
					
					if ("item".equalsIgnoreCase(xpp.getName())) {

						if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
							item = new CommercialRates();
						}
						else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {
							item = new NbuRates();
						}
						else {
							item = new MetalsRates();
						}
						break;
					}
					
					// ////////////////////////////////////////////////
					
					if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
						
						if ("date".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).date = xpp.getText();
							}
						}
						if ("bankName".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).bankName = xpp.getText();
							}
						}
						if ("sourceUrl".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).sourceUrl = xpp.getText();
							}
						}
						if ("codeNumeric".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).codeNumeric = xpp.getText();
							}
						}
						if ("codeAlpha".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).codeAlpha = xpp.getText();
							}
						}
						if ("rateBuy".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).rateBuy = xpp.getText();
							}
						}
						if ("rateBuyDelta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).rateBuyDelta = xpp.getText();
							}
						}
						if ("rateSale".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).rateSale = xpp.getText();
							}
						}
						if ("rateSaleDelta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((CommercialRates)item).rateSaleDelta = xpp.getText();
							}
						}
					}
					else {
						
						if ("date".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).date = xpp.getText();
							}
						}
						if ("code".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).code = xpp.getText();
							}
						}
						if ("char3".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).char3 = xpp.getText();
							}
						}
						if ("size".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).size = xpp.getText();
							}
						}
						if ("name".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).name = xpp.getText();
							}
						}
						if ("rate".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).rate = xpp.getText();
							}
						}
						if ("change".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((Rates)item).change = xpp.getText();
							}
						}											
					}
					break;

				case XmlPullParser.END_TAG:
					
					if ("item".equalsIgnoreCase(xpp.getName())) {
						ratesList.add(item);
					}
					break;

				default:
					break;
				}

				xpp.next();
			}

			if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {

				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, ratesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {
				
				ArrayList<Object> nbuRatesList = new ArrayList<Object>();
				
				for (int count = 0; count < ratesList.size(); count ++) {
					
					NbuRates currency = (NbuRates) ratesList.get(count);
					
					if (!currency.char3.equalsIgnoreCase("tmt") && !currency.char3.equalsIgnoreCase("try") && !currency.char3.equalsIgnoreCase("azn") && !currency.char3.equalsIgnoreCase("xdr")) {
						nbuRatesList.add(currency);
					}
				}
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, nbuRatesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else {
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, ratesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (in != null) {

				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (ratesList == null || ratesList.size() == 0) {
			
			Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
			intent.putExtra("error", "errorLoad");
			intent.putExtra(MainActivity.SOURCE, source);
			intent.putExtra(MainActivity.FROM, from);
			context.sendBroadcast(intent);
		}
	}
}