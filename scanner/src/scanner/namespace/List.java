package scanner.namespace;

import android.app.Activity;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class List extends Activity {
	  private static final int CM_DELETE_ID = 1;
	  ListView lvData;
	  DB db;
	  SimpleCursorAdapter scAdapter;
	  Cursor cursor;

	  /** Called when the activity is first created. */
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    // открываем подключение к БД
	    db = new DB(this);
	    db.open();

	    // получаем курсор
	    refreshCursor();
	    
	    // формируем столбцы сопоставления
	    String[] from = new String[] { DB.COLUMN_IMG, DB.COLUMN_TXT };
	    int[] to = new int[] { R.id.ivImg, R.id.tvText };

	    // создааем адаптер и настраиваем список
	    scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
	    lvData = (ListView) findViewById(R.id.lvData);
	    lvData.setAdapter(scAdapter);

	    // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
	  }

	  // обновляем курсор
	  void refreshCursor() {
	    stopManagingCursor(cursor);
	    cursor = db.getAllData();
	    startManagingCursor(cursor);
	  }

	  // обработка нажатия кнопки
	  public void onButtonClick(View view) {
	    // добавляем запись
	    db.addRec("sometext " + (cursor.getCount() + 1), R.drawable.ic_launcher);
	    // обновляем курсор
	    refreshCursor();
	    // даем адаптеру новый курсор 
	    scAdapter.changeCursor(cursor);
	  }

	  public void onCreateContextMenu(ContextMenu menu, View v,
	      ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	  }
	  
	  public boolean onContextItemSelected(MenuItem item) {
	    if (item.getItemId() == CM_DELETE_ID) {
	      // получаем из пункта контекстного меню данные по пункту списка 
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      // извлекаем id записи и удаляем соответствующую запись в БД
	      db.delRec(acmi.id);
	      // обновляем курсор
	      refreshCursor();
	      // даем адаптеру новый курсор
	      scAdapter.changeCursor(cursor);
	      return true;
	    }
	    return super.onContextItemSelected(item);
	  }
	  
	  protected void onDestroy() {
	    super.onDestroy();
	    // закрываем подключение при выходе
	    db.close();
	  }

}