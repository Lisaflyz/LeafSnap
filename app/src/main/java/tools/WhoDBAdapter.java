package tools;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import beans.Plant;

public class WhoDBAdapter {

	// Column name of table 'plant'
	public static final String PLANT_ID = "pid";
	public static final String PLANT_NAME = "pname";
	public static final String PLANT_DESCRIPTION = "pdesc";
	public static final String PLANT_PICS = "pics";
	public static final String PLANT_HISTGRAMS = "phists";
	public static final String PLANT_DATATIME = "datatime";

	// Primes of DataBase
	private static final String DB_NAME = "WhoDB";
	private static final int DB_VERSION = 3;

	// Table names and SQL to create them
	private static final String DB_TABLE_PLANT = "plant";

	private static final String DB_CREATE_PLANT = "create table plant ("
			+ "pid integer not null primary key autoincrement, "
			+ "pname text not null, " + "pdesc text not null," + "pics text ,"
			+ "phists text not null," + "datatime text not null);";

	// Inference and needs
	private DatabaseHelper WhoDBHelper;
	private SQLiteDatabase WhoDB;
	private final Context WhoContext;

	// Constructor
	public WhoDBAdapter(Context ctx) {
		this.WhoContext = ctx;
	}

	// Get connection
	public WhoDBAdapter open() throws SQLException {
		WhoDBHelper = new DatabaseHelper(WhoContext);
		WhoDB = WhoDBHelper.getWritableDatabase();
		return this;
	}

	// Close connection
	public void close() {
		WhoDBHelper.close();
	}

	// Class to create table
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS plant");
			db.execSQL(DB_CREATE_PLANT);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS plant");
			onCreate(db);
		}
	}

	// Add user
	public long addPlant(String pname, String pdesc, String pics,
			String phists, String datatime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(PLANT_NAME, pname);
		initialValues.put(PLANT_DESCRIPTION, pdesc);
		initialValues.put(PLANT_PICS, pics);
		initialValues.put(PLANT_HISTGRAMS, phists);
		initialValues.put(PLANT_DATATIME, datatime);

		return WhoDB.insert(DB_TABLE_PLANT, null, initialValues);
	}

	// Get all users
	public Cursor getAllPlants() {
		return WhoDB.query(DB_TABLE_PLANT,
				new String[] { PLANT_ID, PLANT_NAME, PLANT_DESCRIPTION,
						PLANT_PICS, PLANT_HISTGRAMS, PLANT_DATATIME }, null,
				null, null, null, null);
	}

	public long addPlant(Plant plant) {
		return addPlant(plant.getPname(), plant.getPdesc(), plant.getPics(),
				plant.getPhists(), plant.getDatatime());
	}

	// delete the plant identified by id
	public int deletePlant(int id) {
		return WhoDB.delete(DB_TABLE_PLANT, "pid = ?",
				new String[] { String.valueOf(id) });
	}

	// update the plant identified by id

	public int updatePlant(Plant plant) {
		ContentValues values = new ContentValues();
		values.put(PLANT_NAME, plant.getPname());
		values.put(PLANT_DESCRIPTION, plant.getPdesc());
		values.put(PLANT_DATATIME, plant.getDatatime());
		values.put(PLANT_PICS, plant.getPics());
		return WhoDB.update(DB_TABLE_PLANT, values, "pid = ?",
				new String[] { String.valueOf(plant.getPid()) });
	}

	// search the plants by name
	@SuppressLint("NewApi")
	public Cursor searchPlants(String query) {
		return WhoDB.query(false, DB_TABLE_PLANT, null, "pname like ?",
				new String[] { "%"+query+"%" }, null, null, null, null, null);
	}
}
