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

	    // ��������� ����������� � ��
	    db = new DB(this);
	    db.open();

	    // �������� ������
	    refreshCursor();
	    
	    // ��������� ������� �������������
	    String[] from = new String[] { DB.COLUMN_IMG, DB.COLUMN_TXT };
	    int[] to = new int[] { R.id.ivImg, R.id.tvText };

	    // �������� ������� � ����������� ������
	    scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
	    lvData = (ListView) findViewById(R.id.lvData);
	    lvData.setAdapter(scAdapter);

	    // ��������� ����������� ���� � ������
	    registerForContextMenu(lvData);
	  }

	  // ��������� ������
	  void refreshCursor() {
	    stopManagingCursor(cursor);
	    cursor = db.getAllData();
	    startManagingCursor(cursor);
	  }

	  // ��������� ������� ������
	  public void onButtonClick(View view) {
	    // ��������� ������
	    db.addRec("sometext " + (cursor.getCount() + 1), R.drawable.ic_launcher);
	    // ��������� ������
	    refreshCursor();
	    // ���� �������� ����� ������ 
	    scAdapter.changeCursor(cursor);
	  }

	  public void onCreateContextMenu(ContextMenu menu, View v,
	      ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	  }
	  
	  public boolean onContextItemSelected(MenuItem item) {
	    if (item.getItemId() == CM_DELETE_ID) {
	      // �������� �� ������ ������������ ���� ������ �� ������ ������ 
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      // ��������� id ������ � ������� ��������������� ������ � ��
	      db.delRec(acmi.id);
	      // ��������� ������
	      refreshCursor();
	      // ���� �������� ����� ������
	      scAdapter.changeCursor(cursor);
	      return true;
	    }
	    return super.onContextItemSelected(item);
	  }
	  
	  protected void onDestroy() {
	    super.onDestroy();
	    // ��������� ����������� ��� ������
	    db.close();
	  }

}