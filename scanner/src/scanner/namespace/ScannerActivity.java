package scanner.namespace;

/**import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class ScannerActivity extends Activity {
    /** Called when the activity is first created. */
    /**@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** вывод на экран найденных точек*/
       /** final TextView text = (TextView)findViewById(R.id.TextView01);
    }
}*/



import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
 
public class ScannerActivity extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener(){

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}};
    //String context, provider;
 
 
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       mainText = (TextView) findViewById(R.id.TextView01);
       mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       receiverWifi = new WifiReceiver();
       registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       mainWifi.startScan();
       mainText.setText("\nStarting Scan...\n");
       
       //context = Context.LOCATION_SERVICE;
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
       //provider = LocationManager.GPS_PROVIDER;
       //location = locationManager.getLastKnownLocation(provider);
       
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }
 
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();
        mainText.setText("Starting Scan");
        
       // location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        return super.onMenuItemSelected(featureId, item);
    }
 
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }
 
    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
 
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
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            sb.append(location.getLatitude());
            sb.append("\n");
            sb.append(location.getLongitude());
            mainText.setText(sb);
        }
    }
}