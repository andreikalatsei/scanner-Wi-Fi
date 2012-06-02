package scanner.namespace;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle; 
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Maps extends MapActivity {
	
	public MapView mapView;
    public MapController mapController;
    
    List<Overlay> mapOverlays;
    Drawable drawable;
    MyItemizedOverlay itemizedoverlay;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //скрываем заголовок
        setContentView(R.layout.map);
        
        //инициализируем карту
        mapView = (MapView) findViewById(R.id.mapView);
        //добавляем стандартные кнопки зума
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        //создаём GeoPoint с координатами 
        Intent intent = getIntent();
        GeoPoint point = new GeoPoint((int) (intent.getDoubleExtra("latitude", 0) * 1E6), (int) (intent.getDoubleExtra("longitude", 0)* 1E6));
        //и передвигаем карту в эту точку
        mapController.animateTo(point);
        mapController.setZoom(16);   
        
        mapOverlays = mapView.getOverlays();
        drawable =  this.getResources().getDrawable(R.drawable.kwifimanager);    
        
        addPoints();
    }
    
    public void addPoints (){  	
        // открываем подключение к БД
        DbAdapter db = new DbAdapter(getBaseContext());
        db.open();
		   
		    Cursor c = db.fetchAll();
		    if (c.moveToFirst()) {
		    	int columnResult = c.getColumnIndex(DbAdapter.KEY_FLAG_RESULT);
		    	 int columnX1 = c.getColumnIndex(DbAdapter.KEY_X1);
		    	 int colunmY1 = c.getColumnIndex(DbAdapter.KEY_Y1);
		    	 int columnResultX =  c.getColumnIndex(DbAdapter.KEY_X3);
		    	 int columnResultY =  c.getColumnIndex(DbAdapter.KEY_Y3);
		    	 int columnSSID = c.getColumnIndex(DbAdapter.KEY_SSID);
		    	 int columnBSSID = c.getColumnIndex(DbAdapter.KEY_BSSID);

		    	 do {
		    		 GeoPoint gp;
		    		 if (c.getInt(columnResult) == 0){
		    			 gp = new GeoPoint((int) (c.getDouble(columnX1) * 1E6), (int) (c.getDouble(colunmY1) * 1E6));
		    		 }
		    		 else{
		    			 gp = new GeoPoint((int) (c.getDouble(columnResultX) * 1E6), (int) (c.getDouble(columnResultY) * 1E6));
		    		 }	    		
		    		 itemizedoverlay = new MyItemizedOverlay(drawable, this);
		    	     OverlayItem overlayitem = new OverlayItem(gp, c.getString(columnSSID), c.getString(columnBSSID));
		    	     itemizedoverlay.addOverlay(overlayitem);
		    	     mapOverlays.add(itemizedoverlay);
		    	 } while (c.moveToNext());    	 
		    }
		    db.close();
    }
 
	 @Override
	 protected boolean isRouteDisplayed() {
	  // TODO Auto-generated method stub
	  return false;
	 }
	   
	 public boolean onCreateOptionsMenu(Menu menu) {     
    	 menu.add (Menu.FIRST, 1, 1, "Вид со спутника вкл/выкл");  
    	 menu.add(Menu.FIRST, 2, 2, "Обновить");
    	 return super.onCreateOptionsMenu(menu);
	 }
	 
	 public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    	switch (item.getItemId()) {
	    		case 1:
	    			if (mapView.isSatellite())
	    				mapView.setSatellite(false);
	    			else
	    				mapView.setSatellite(true);	    			
			        break;   
	    		case 2:
	    			mapOverlays.remove(itemizedoverlay);
	    			addPoints();
	    			break;
		    }
	        return super.onMenuItemSelected(featureId, item);
	    }
}