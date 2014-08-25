package dn.ivan.actionbarexample.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import dn.ivan.actionbarexample.MainActivity;

public class DataManager {
	
	protected DBHelper dbh;
	protected SQLiteDatabase db;
	
	public ArrayList<NbuRatesHolderForChart> selectRatesNbu(Context context, String currency, String begin, String end) {
		
		ArrayList<NbuRatesHolderForChart> rates = new ArrayList<DataManager.NbuRatesHolderForChart>();
		
		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();
		
		// //////////////////////////////////////////////////
		
		SimpleDateFormat dateFormatForResult = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		SimpleDateFormat dateFormatBegin = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.getDefault());
		SimpleDateFormat dateFormatEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59", Locale.getDefault());
		
		try {
			Cursor c = db.rawQuery("SELECT created_at, rate FROM NBU_RATES WHERE currency = '" + currency + "'" + " AND created_at BETWEEN '" + dateFormatBegin.format(dateFormat.parse(begin)) + "' AND '" + dateFormatEnd.format(dateFormat.parse(end)) + "'", null);
			
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						
						NbuRatesHolderForChart rate = new NbuRatesHolderForChart();
						
						rate.date = dateFormatForResult.parse(c.getString(c.getColumnIndex("created_at")));
						rate.rate = c.getDouble(c.getColumnIndex("rate"));
						
						rates.add(rate);
					}
					while (c.moveToNext());
				}
			}
		}
		catch (Exception e) {			
			Log.v("PARSE_RAW_Query", e.toString());
		}
		
		// //////////////////////////////////////////////////
		
		dbh.close();
		
		return rates;
	}

	@SuppressWarnings("unchecked")
	public void saveNBURates2DB(Context context, ArrayList<Object> rates) {

		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();

		// //////////////////////////////////////////////
		
		new MyTask().execute(rates);
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////
	
	class MyTask extends AsyncTask<ArrayList<Object>, Void, Void> {

		@Override
		protected void onPreExecute() {			
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			
			super.onPostExecute(result);
			dbh.close();
		}

		@Override
		protected Void doInBackground(ArrayList<Object>... arg0) {
			
			ArrayList<Object> rates = arg0[0];
			
			ContentValues cv = new ContentValues();
			
			Date date = new Date();

			for (int i = 0; rates != null && i < rates.size(); i++) {

				NbuRates rate = (NbuRates) rates.get(i);

				cv.clear();
				
				cv.put("created_at", getDateTime(date));
				cv.put("currency", rate.char3);
				cv.put("rate", Double.valueOf(rate.rate));
				
				db.insert("NBU_RATES", null, cv);
			}
			
			return null;
		}
		
		private String getDateTime(Date date) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	        return dateFormat.format(date);
		}
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class NbuRatesHolderForChart {
		
		public Date date;
		public double rate;
		
		@Override
		public String toString() {
			return "NbuRatesHolderForChart [date=" + date + ", rate=" + rate
					+ "]";
		}		
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void saveValue2Pref(Context context, String code, String value) {
		
		SharedPreferences shared = context.getSharedPreferences(code, MainActivity.MODE_PRIVATE);
		Editor ed = shared.edit();
		ed.remove(code);
		ed.putString(code, value);
		ed.commit();
	}

	public static String loadValueFromPref(Context context, String code) {
		
		SharedPreferences shared = context.getSharedPreferences(code, MainActivity.MODE_PRIVATE);
		String value = shared.getString(code, "");
		return value;
	}
}