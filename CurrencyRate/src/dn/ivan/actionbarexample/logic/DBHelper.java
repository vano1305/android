package dn.ivan.actionbarexample.logic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final int    DB_VERSION = 1;
	private static final String DB_NBU = "NBU_RATES";

	public DBHelper(Context context) {
		super(context, DB_NBU, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table NBU_RATES ("
				+ "id INTEGER primary key autoincrement,"
				+ "created_at DATETIME,"
				+ "currency TEXT,"
				+ "rate DOUBLE" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}