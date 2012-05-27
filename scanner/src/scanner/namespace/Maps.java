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
import android.location.LocationManager;
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
        drawable =  this.getResources().getDrawable(R.drawable.ic_launcher);
        
        
        new Runnable() {
            public void run() {
               
            }
        };
        // открываем подключение к БД
		  /*  DB db = new DB(this);
		    db.open();
		   
		    Cursor c = db.getAllData();
		    if (c.moveToFirst()) {
		    	 int columnLat = c.getColumnIndex(DB.COLUMN_LAT);
		    	 int colunmLong = c.getColumnIndex(DB.COLUMN_LONG);
		    	 do {
		    		 GeoPoint gp = new GeoPoint((int) (c.getDouble(columnLat) * 1E6), (int) (c.getDouble(colunmLong)* 1E6));
		    		 itemizedoverlay = new MyItemizedOverlay(drawable);
		    	     OverlayItem overlayitem = new OverlayItem(gp, "", "");
		    	     itemizedoverlay.addOverlay(overlayitem);
		    	     mapOverlays.add(itemizedoverlay);
		    	 } while (c.moveToNext());    	 
		    }
		    db.close();*/
        
        
        
        /*itemizedoverlay = new MyItemizedOverlay(drawable);
        OverlayItem overlayitem = new OverlayItem(point, "", "");
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);*/
    }
 
	 @Override
	 protected boolean isRouteDisplayed() {
	  // TODO Auto-generated method stub
	  return false;
	 }
	   
	 public boolean onCreateOptionsMenu(Menu menu) {     
    	 menu.add (Menu.FIRST, 1, 1, "Выход");  
    	 return super.onCreateOptionsMenu(menu);
	 }
	 
	 public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    	switch (item.getItemId()) {
	    		case 1:
			       finish();
			        break;      
		    }
	        return super.onMenuItemSelected(featureId, item);
	    }
}