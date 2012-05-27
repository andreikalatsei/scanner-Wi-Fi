package scanner.namespace;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class ScannerActivity extends Activity {
	TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    boolean wifiState; //хранение исходного состояния wifi в телефоне
    
    String[] providers = new String[] 
    	    { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
    SQLiteDatabase db;
    private static final String TAG = "myLogs";
    Location location;
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener(){

		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    };
 
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE); //скрываем заголовок
       setContentView(R.layout.main);
       mainText = (TextView) findViewById(R.id.TextView01);
       mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       receiverWifi = new WifiReceiver();
       registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       //если wifi в телефоне выключен, включаем его
       wifiState = mainWifi.isWifiEnabled();
       if (!wifiState){
    	   mainWifi.setWifiEnabled(true);
       }
       
       // создаем объект для создания и управления версиями БД
      // dbHelper = DBHelper.getInstance();
       mainWifi.startScan();
       mainText.setText("\nStarting Scan...\n");
       
       //context = Context.LOCATION_SERVICE;
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       for (String provider : providers) {
    	   locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
       }
       //provider = LocationManager.GPS_PROVIDER;
       //location = locationManager.getLastKnownLocation(provider);
       
       Log.d(TAG,"------end oncreate------");
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {     
    	 menu.add (Menu.FIRST, 1, 1, "Обновить");  
         menu.add (Menu.FIRST, 2, 2, "Карта");           
         menu.add (Menu.FIRST, 3, 3, "Просмотр базы");  
         menu.add (Menu.FIRST, 4, 4, "Удалить базу");  
         menu.add (Menu.FIRST, 5, 5, "Выход");  
           
         //menu.add (Menu.CATEGORY_SECONDARY, 6, 6, "Item 1");  
         //menu.add (Menu.CATEGORY_SECONDARY, 7, 7, "Item 2");  
         //menu.add (Menu.CATEGORY_SECONDARY, 8, 8, "Item 3");  
        return super.onCreateOptionsMenu(menu);
    }
 
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
    		case 1:
		        mainWifi.startScan();
		        mainText.setText("Starting Scan");
		        
		        // location = locationManager.getLastKnownLocation(provider);
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        break;
	        
    		case 2:
    			Intent intent = new Intent(this, Maps.class);
    			intent.putExtra("latitude", location.getLatitude());
    			intent.putExtra("longitude", location.getLongitude());
    		    startActivity(intent);
    		    break;
    		case 3:
    			intent = new Intent (this, ListBD.class);
    			startActivity(intent);
    			break;
    		case 4:
    			DbAdapter dbHelper = new DbAdapter(getBaseContext());
                dbHelper.open();
                dbHelper.deleteTable();
                dbHelper.close();
    			break;
    		case 5:
    			finish();
	    }
        return super.onMenuItemSelected(featureId, item);
    }
 
    protected void onPause() {
    	// выключаем отслеживание
        locationManager.removeUpdates(locationListener);
        unregisterReceiver(receiverWifi);
        super.onPause();
    }
 
    protected void onResume() {
    	//включаем отслеживание
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    
    /*protected void onStop(){
    	if(wifiState){
    		mainWifi.setWifiEnabled(false);
    	}
    }*/
 
    class WifiReceiver extends BroadcastReceiver {
    	
		public void onReceive(Context c, Intent intent) {
			Log.d(TAG,"------wifi receiver----");
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            
            location = null;
            for (String provider : providers) {
            	location = locationManager.getLastKnownLocation(provider);
            		if (location != null) {
            	    break;
            	   }
            }
            
           // location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // подключаемся к БД
            DbAdapter dbHelper = new DbAdapter(getBaseContext());
            dbHelper.open();
                       
            for(int i = 0; i < wifiList.size(); i++){
                sb.append(new Integer(i+1).toString() + ".");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n");

               sb.append(dbHelper.createRecord(wifiList.get(i).BSSID, wifiList.get(i).SSID, 
            		   location.getLatitude(), location.getLongitude(), wifiList.get(i).level, wifiList.get(i).frequency));
               sb.append("\n");
            }
            sb.append("\n");
          
            if (location != null){
	            sb.append(location.getLatitude());
	            sb.append("\n");
	            sb.append(location.getLongitude());
	            sb.append("\n");
	            if (location.hasAccuracy()){
	            	sb.append("Accuracy ");
	            	sb.append(location.getAccuracy());
	            	sb.append("\n");
	            }
	            if (location.hasAltitude()){
	            	sb.append(location.getAltitude());
	            }
            }
            else
            	Toast.makeText(getApplicationContext(), "Не удалось получить координаты", Toast.LENGTH_LONG).show();
            Cursor cur = dbHelper.fetchAll();
            if (cur.moveToFirst()){
	            sb.append("\n");
	            int nameColSSID = cur.getColumnIndex(dbHelper.KEY_SSID);
	            for (int i = 0; i < cur.getCount(); i++, cur.moveToNext()){
	                sb.append(cur.getString(nameColSSID));
	                sb.append("\n");
	            }
            }
            mainText.setText(sb);
            dbHelper.close();
        }
    }
}