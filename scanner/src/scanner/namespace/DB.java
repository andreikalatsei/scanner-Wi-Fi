package scanner.namespace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
	  
	  private static final String DB_NAME = "myDatabase";
	  private static final int DB_VERSION = 1;
	  private static final String DB_TABLE = "mytable";
	  
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_NAME = "name";
	  public static final String COLUMN_LAT = "latitude";
	  public static final String COLUMN_LONG = "longitude";
	  
	  private static final String DB_CREATE = 
			  "create table mytable " + DB_TABLE + "(" +
					  COLUMN_ID + " integer primary key autoincrement, " +
					  COLUMN_NAME + "text," +
					  COLUMN_LAT + "real," +
					  COLUMN_LONG + "real);";
			          	  
	  private final Context mCtx;
	  
	  
	  private DBHelper mDBHelper;
	  private SQLiteDatabase mDB;
	  
	  public DB(Context ctx) {
	    mCtx = ctx;
	  }
	  
	  // ������� �����������
	  public void open() {
	    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
	    mDB = mDBHelper.getWritableDatabase();
	  }
	  
	  // ������� �����������
	  public void close() {
	    if (mDBHelper!=null) mDBHelper.close();
	  }
	  
	  // �������� ��� ������ �� ������� DB_TABLE
	  public Cursor getAllData() {
	    return mDB.query(DB_TABLE, null, null, null, null, null, null);
	  }
	  
	  // �������� ������ � DB_TABLE
	  public void addRec(String name, double latitude, double longitude) {
	    ContentValues cv = new ContentValues();
	    cv.put("name", name);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
	    mDB.insert(DB_TABLE, null, cv);
	  }
	  
	  // ������� ������ �� DB_TABLE
	  public void delRec(long id) {
	    mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
	  }
	  
	  // ����� �� �������� � ���������� ��
	  private class DBHelper extends SQLiteOpenHelper {

	    public DBHelper(Context context, String name, CursorFactory factory,int version) {
	      super(context, name, factory, version);
	    }
	    
	    public DBHelper(Context context) {
		      // ����������� �����������
		      super(context, DB_NAME, null, DB_VERSION);
	    }
		      
	    // ������� � ��������� ��
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      db.execSQL(DB_CREATE);
	      
	      /*ContentValues cv = new ContentValues();
	      for (int i = 1; i < 5; i++) {
	        cv.put(COLUMN_TXT, "sometext " + i);
	        cv.put(COLUMN_IMG, R.drawable.ic_launcher);
	        db.insert(DB_TABLE, null, cv);
	      }*/
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    }
	  }
	}