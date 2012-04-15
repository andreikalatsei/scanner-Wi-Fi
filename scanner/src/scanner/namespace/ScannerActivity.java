package scanner.namespace;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
       mainWifi.startScan();
       mainText.setText("\nStarting Scan...\n");
       
       //context = Context.LOCATION_SERVICE;
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
       //provider = LocationManager.GPS_PROVIDER;
       //location = locationManager.getLastKnownLocation(provider);
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Обновить");
        menu.add(0, 1, 0, "Карта");
        return super.onCreateOptionsMenu(menu);
    }
 
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
    		case 0:
		        mainWifi.startScan();
		        mainText.setText("Starting Scan");
		        
		        // location = locationManager.getLastKnownLocation(provider);
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        break;
	        
    		case 1:
    			Intent intent = new Intent(this, Maps.class);
    			intent.putExtra("latitude", location.getLatitude());
    			intent.putExtra("longitude", location.getLongitude());
    		    startActivity(intent);
    		    break;
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
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
                sb.append(new Integer(i+1).toString() + ".");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n");
            }
            sb.append("\n");
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
	            sb.append(location.getLatitude());
	            sb.append("\n");
	            sb.append(location.getLongitude());
	            sb.append("\n");
	            if (location.hasAccuracy()){
	            	sb.append(location.getAccuracy());
	            	sb.append("\n");
	            }
	            if (location.hasAltitude()){
	            	sb.append(location.getAltitude());
	            }
            }
            else
            	Toast.makeText(getApplicationContext(), "Не удалось получить координаты", Toast.LENGTH_LONG).show();
            mainText.setText(sb);
        }
    }
}