package scanner.namespace;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {	
	 private static final String DATABASE_NAME = "myDatabase";
	 private static final int DATABASE_VERSION = 1;


	 public DBHelper(Context context) {
	      // конструктор суперкласса
	      super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// создаем таблицу с полями
	      db.execSQL("create table mytable ("
	          + "id integer primary key autoincrement," 
	          + "name text,"
	          + "latitude real," 
	          + "longitude real);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
		//db.execSQL("DROP TABLE IF EXISTS myDatabase");
		//onCreate(db);
	}

}
