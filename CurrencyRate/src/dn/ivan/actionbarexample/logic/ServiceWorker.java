package dn.ivan.actionbarexample.logic;

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

public class ServiceWorker implements Runnable {

	ArrayList<Object> ratesList = new ArrayList<Object>();
	
	// //////////////////////////////////////////////////

	private Context context;
	private String source = "";
	private String from = "";
	private String regionCode = "";//Для топлива
	private String cityCode = "";//Для чёрного рынка
	
	/*
	 * Для загрузки истории
	 * */
	private String currencyCode = "";
	private String date1 = "";
	private String date2 = "";
	
	public ServiceWorker(Context context, String source, String from, String regionCode, String cityCode, String currencyCode, String date1, String date2) {

		this.context = context;
		this.source = source;
		this.from = from;
		this.regionCode = regionCode;
		this.cityCode = cityCode;
		
		this.currencyCode = currencyCode;
		this.date1 = date1;
		this.date2 = date2;
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
				//request = new HttpGet("http://bank-ua.com/export/exchange_rate_cash.xml");
				request = new HttpGet("http://currencyrates.jelastic.neohost.net/rates/manage_rates/commercial");
			}
			else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {				
				request = new HttpGet("http://bank-ua.com/export/currrate.xml");	
				//request = new HttpGet("http://currencyrates.jelastic.neohost.net/rates/manage_rates/nbu");
			}
			else if (MainActivity.METALS_SOURCE.equalsIgnoreCase(source)) {
				request = new HttpGet("http://bank-ua.com/export/metalrate.xml");
			}
			else if (MainActivity.FUEL_SOURCE.equalsIgnoreCase(source)) {
				request = new HttpGet("http://currencyrates.jelastic.neohost.net/rates/manage_rates/fuel" + ("".equalsIgnoreCase(regionCode)? "": ("?region=" + regionCode)));
			}
			else if (MainActivity.BLACK_MARKET_SOURCE.equalsIgnoreCase(source)) {
				request = new HttpGet("http://currencyrates.jelastic.neohost.net/rates/manage_rates/black_market" + ("".equalsIgnoreCase(cityCode)? "": ("?city=" + cityCode)));
			}
			else if (MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(source)) {
				request = new HttpGet("http://currencyrates.jelastic.neohost.net/rates/manage_rates/nbu_history?currency=" + currencyCode + "&date1=" + date1 + "&date2=" + date2);
			}

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source) || MainActivity.FUEL_SOURCE.equalsIgnoreCase(source) || MainActivity.BLACK_MARKET_SOURCE.equalsIgnoreCase(source) || MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(source)) {
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
					
					if ("item".equalsIgnoreCase(xpp.getName()) || "NbuHistoryItem".equalsIgnoreCase(xpp.getName())) {

						if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
							item = new CommercialRates();
						}
						else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {
							item = new NbuRates();
						}
						else if (MainActivity.METALS_SOURCE.equalsIgnoreCase(source)) {
							item = new MetalsRates();
						}
						else if (MainActivity.FUEL_SOURCE.equalsIgnoreCase(source)) {
							item = new FuelItem();
						}
						else if (MainActivity.BLACK_MARKET_SOURCE.equalsIgnoreCase(source)) {
							item = new BlackMarketItem();
						}
						else if (MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(source)) {
							item = new NbuHistoryItem();
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
					else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source) || MainActivity.METALS_SOURCE.equalsIgnoreCase(source)) {
						
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
					else if (MainActivity.FUEL_SOURCE.equalsIgnoreCase(source)) {
						
						if ("date".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).date = xpp.getText();
							}
						}
						if ("name".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).name = xpp.getText();
							}
						}
						if ("code".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).code = xpp.getText();
							}
						}
						if ("a_80".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_80 = xpp.getText();
							}
						}
						if ("a_92".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_92 = xpp.getText();
							}
						}
						if ("a_95".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_95 = xpp.getText();
							}
						}
						if ("dt".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).dt = xpp.getText();
							}
						}
						if ("a_80_delta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_80_delta = xpp.getText();
							}
						}
						if ("a_92_delta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_92_delta = xpp.getText();
							}
						}
						if ("a_95_delta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).a_95_delta = xpp.getText();
							}
						}
						if ("dt_delta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((FuelItem)item).dt_delta = xpp.getText();
							}
						}
					}
					else if (MainActivity.BLACK_MARKET_SOURCE.equalsIgnoreCase(source)) {
						
						if ("opCode".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).opCode = xpp.getText();
							}
						}
						if ("date".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).date = xpp.getText();
							}
						}
						if ("cityCode".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).cityCode = xpp.getText();
							}
						}
						if ("currencyCode".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).currencyCode = xpp.getText();
							}
						}
						if ("rate".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).rate = xpp.getText();
							}
						}
						if ("rate_delta".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((BlackMarketItem)item).rate_delta = xpp.getText();
							}
						}
					}
					else if (MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(source)) {
						
						if ("currencyCode".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((NbuHistoryItem)item).currencyCode = xpp.getText();
							}
						}
						if ("date1".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((NbuHistoryItem)item).date1 = xpp.getText();
							}
						}
						if ("date2".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((NbuHistoryItem)item).date2 = xpp.getText();
							}
						}
						if ("history".equalsIgnoreCase(xpp.getName())) {
							if (xpp.next() == XmlPullParser.TEXT) {
								((NbuHistoryItem)item).history = xpp.getText();
							}
						}
					}
					
					break;

				case XmlPullParser.END_TAG:
					
					if ("item".equalsIgnoreCase(xpp.getName()) || "NbuHistoryItem".equalsIgnoreCase(xpp.getName())) {
						ratesList.add(item);
					}
					
					break;

				default:
					break;
				}

				xpp.next();
			}

			if (MainActivity.COMMERCIAL_SOURCE.equalsIgnoreCase(source)) {
				
				ArrayList<Object> commercialRatesList = new ArrayList<Object>();
				
				for (int count = 0; count < ratesList.size(); count ++) {
					
					CommercialRates currency = (CommercialRates) ratesList.get(count);
					
					if (!"".equalsIgnoreCase(currency.sourceUrl) && currency.sourceUrl.indexOf("pivdennyi") == -1) {
						commercialRatesList.add(currency);
					}
				}

				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, commercialRatesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.NBU_SOURCE.equalsIgnoreCase(source)) {
				
				ArrayList<Object> nbuRatesList = new ArrayList<Object>();
				
				for (int count = 0; count < ratesList.size(); count ++) {
					
					NbuRates currency = (NbuRates) ratesList.get(count);
					
					if (!"".equalsIgnoreCase(currency.char3) && !currency.char3.equalsIgnoreCase("tmt") && !currency.char3.equalsIgnoreCase("try") && !currency.char3.equalsIgnoreCase("azn") && !currency.char3.equalsIgnoreCase("xdr")) {
						nbuRatesList.add(currency);
					}
				}
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, nbuRatesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.METALS_SOURCE.equalsIgnoreCase(source)) {
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, ratesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.FUEL_SOURCE.equalsIgnoreCase(source)) {
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, ratesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.BLACK_MARKET_SOURCE.equalsIgnoreCase(source)) {
				
				Intent intent = new Intent((from.equalsIgnoreCase(MainActivity.FROM_APPLICATION) || from.equalsIgnoreCase(MainActivity.FROM_WIDGET))? MainActivity.FINISH_LOAD: MainActivity.UPDATE_HISTORY);
				intent.putExtra(MainActivity.RATES, ratesList);
				intent.putExtra(MainActivity.SOURCE, source);
				intent.putExtra(MainActivity.FROM, from);
				context.sendBroadcast(intent);
			}
			else if (MainActivity.NBU_HISTORY_SOURCE.equalsIgnoreCase(source)) {
				
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